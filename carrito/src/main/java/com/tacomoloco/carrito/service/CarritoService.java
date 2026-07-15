package com.tacomoloco.carrito.service;

import com.tacomoloco.carrito.client.CatalogoClient;
import com.tacomoloco.carrito.client.PedidoClient;
import com.tacomoloco.carrito.dto.*;
import com.tacomoloco.carrito.entity.Carrito;
import com.tacomoloco.carrito.entity.ItemCarrito;
import com.tacomoloco.carrito.repository.CarritoRepository;
import com.tacomoloco.carrito.repository.ItemCarritoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CatalogoClient catalogoClient;
    private final PedidoClient pedidoClient;

    @Transactional
    public CarritoResponseDTO obtenerOCrear(Long clienteId) {
        Carrito carrito = carritoRepository.findByClienteIdAndActivoTrue(clienteId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setClienteId(clienteId);
                    return carritoRepository.save(nuevo);
                });
        return convertirADTO(carrito);
    }

    @Transactional
    public CarritoResponseDTO agregarItem(Long clienteId, AgregarItemRequestDTO request) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ProductoDTO producto = catalogoClient.getProductoById(request.getProductoId());
        if (producto == null || !producto.getDisponible()) {
            throw new IllegalArgumentException("Producto no encontrado o no disponible: " + request.getProductoId());
        }

        ItemCarrito existente = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId())
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + request.getCantidad());
            existente.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(existente.getCantidad())));
            itemCarritoRepository.save(existente);
        } else {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProductoId(request.getProductoId());
            item.setCantidad(request.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            item.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(request.getCantidad())));
            item.setNombreProducto(producto.getNombre());
            item.setImagenUrl(producto.getImagenUrl());
            itemCarritoRepository.save(item);
        }

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO actualizarCantidadItem(Long clienteId, Long itemId, Integer nuevaCantidad) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado con ID: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del cliente");
        }

        if (nuevaCantidad <= 0) {
            itemCarritoRepository.delete(item);
        } else {
            item.setCantidad(nuevaCantidad);
            item.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
            itemCarritoRepository.save(item);
        }

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO eliminarItem(Long clienteId, Long itemId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado con ID: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del cliente");
        }

        itemCarritoRepository.delete(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public void vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);
        itemCarritoRepository.deleteAll(itemCarritoRepository.findByCarritoId(carrito.getId()));
    }

    @Transactional
    public CarritoResponseDTO obtenerCarrito(Long clienteId) {
        Carrito carrito = carritoRepository.findByClienteIdAndActivoTrue(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("No hay carrito activo para el cliente: " + clienteId));
        return convertirADTO(carrito);
    }

    @Transactional
    public PedidoCheckoutResponseDTO checkout(Long clienteId, String notasCliente) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito esta vacio");
        }

        PedidoCheckoutRequestDTO request = PedidoCheckoutRequestDTO.builder()
                .clienteId(clienteId)
                .notasCliente(notasCliente)
                .total(carrito.calcularTotal())
                .detalles(carrito.getItems().stream().map(item ->
                        PedidoCheckoutRequestDTO.DetalleCheckoutDTO.builder()
                                .productoId(item.getProductoId())
                                .nombreProducto(item.getNombreProducto())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .subtotal(item.getSubtotal())
                                .build()
                ).toList())
                .build();

        PedidoCheckoutResponseDTO response = pedidoClient.checkout(request);

        vaciarCarrito(clienteId);

        return response;
    }

    private Carrito obtenerCarritoActivo(Long clienteId) {
        return carritoRepository.findByClienteIdAndActivoTrue(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("No hay carrito activo para el cliente: " + clienteId));
    }

    private CarritoResponseDTO convertirADTO(Carrito carrito) {
        return CarritoResponseDTO.builder()
                .id(carrito.getId())
                .clienteId(carrito.getClienteId())
                .fechaCreacion(carrito.getFechaCreacion())
                .fechaActualizacion(carrito.getFechaActualizacion())
                .total(carrito.calcularTotal())
                .cantidadItems(carrito.getItems() != null ? carrito.getItems().size() : 0)
                .items(carrito.getItems() != null ? carrito.getItems().stream()
                        .map(item -> CarritoResponseDTO.ItemCarritoResponseDTO.builder()
                                .id(item.getId())
                                .productoId(item.getProductoId())
                                .nombreProducto(item.getNombreProducto())
                                .imagenUrl(item.getImagenUrl())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList() : List.of())
                .build();
    }
}