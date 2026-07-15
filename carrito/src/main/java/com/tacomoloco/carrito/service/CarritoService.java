package com.tacomoloco.carrito.service;

import com.tacomoloco.carrito.client.CatalogoClient;
import com.tacomoloco.carrito.client.PedidoClient;
import com.tacomoloco.carrito.dto.*;
import com.tacomoloco.carrito.entity.*;
import com.tacomoloco.carrito.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ItemPersonalizacionRepository itemPersonalizacionRepository;
    private final GrupoPedidoRepository grupoPedidoRepository;
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

        GrupoPedido grupo = null;
        if (request.getGrupoPedidoId() != null) {
            grupo = grupoPedidoRepository.findById(request.getGrupoPedidoId())
                    .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + request.getGrupoPedidoId()));
            if (!grupo.getCarrito().getId().equals(carrito.getId())) {
                throw new IllegalArgumentException("El grupo no pertenece al carrito del cliente");
            }
        }

        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setGrupoPedido(grupo);
        item.setProductoId(request.getProductoId());
        item.setCantidad(request.getCantidad());
        item.setPrecioUnitario(producto.getPrecio());
        item.setNombreProducto(producto.getNombre());
        item.setImagenUrl(producto.getImagenUrl());
        item = itemCarritoRepository.save(item);

        if (request.getPersonalizaciones() != null && !request.getPersonalizaciones().isEmpty()) {
            List<IngredienteCatalogoDTO> ingredientesProducto = catalogoClient.getIngredientesByProductoId(request.getProductoId());

            for (AgregarItemRequestDTO.PersonalizacionRequestDTO persReq : request.getPersonalizaciones()) {
                ItemPersonalizacion pers = new ItemPersonalizacion();
                pers.setItemCarrito(item);
                pers.setIngredienteId(persReq.getIngredienteId());
                pers.setTipo(ItemPersonalizacion.TipoPersonalizacion.valueOf(persReq.getTipo().toUpperCase()));

                IngredienteCatalogoDTO match = ingredientesProducto.stream()
                        .filter(i -> i.getIngredienteId().equals(persReq.getIngredienteId()))
                        .findFirst().orElse(null);

                pers.setNombreIngrediente(match != null ? match.getNombreIngrediente() : "Ingrediente #" + persReq.getIngredienteId());
                pers.setCostoExtra(match != null ? match.getCostoUnitario() : BigDecimal.ZERO);

                itemPersonalizacionRepository.save(pers);
            }
        }

        item.setSubtotal(item.calcularSubtotalConExtras());
        itemCarritoRepository.save(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO agregarPersonalizacionAItem(Long clienteId, Long itemId, AgregarItemRequestDTO.PersonalizacionRequestDTO request) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado con ID: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del cliente");
        }

        List<IngredienteCatalogoDTO> ingredientesProducto = catalogoClient.getIngredientesByProductoId(item.getProductoId());

        ItemPersonalizacion pers = new ItemPersonalizacion();
        pers.setItemCarrito(item);
        pers.setIngredienteId(request.getIngredienteId());
        pers.setTipo(ItemPersonalizacion.TipoPersonalizacion.valueOf(request.getTipo().toUpperCase()));

        IngredienteCatalogoDTO match = ingredientesProducto.stream()
                .filter(i -> i.getIngredienteId().equals(request.getIngredienteId()))
                .findFirst().orElse(null);

        pers.setNombreIngrediente(match != null ? match.getNombreIngrediente() : "Ingrediente #" + request.getIngredienteId());
        pers.setCostoExtra(match != null ? match.getCostoUnitario() : BigDecimal.ZERO);

        itemPersonalizacionRepository.save(pers);

        item.setSubtotal(item.calcularSubtotalConExtras());
        itemCarritoRepository.save(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO eliminarPersonalizacion(Long clienteId, Long personalizacionId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ItemPersonalizacion pers = itemPersonalizacionRepository.findById(personalizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Personalizacion no encontrada"));

        ItemCarrito item = pers.getItemCarrito();
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("La personalizacion no pertenece al carrito del cliente");
        }

        itemPersonalizacionRepository.delete(pers);

        item.setSubtotal(item.calcularSubtotalConExtras());
        itemCarritoRepository.save(item);

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
            itemPersonalizacionRepository.deleteByItemCarritoId(itemId);
            itemCarritoRepository.delete(item);
        } else {
            item.setCantidad(nuevaCantidad);
            item.setSubtotal(item.calcularSubtotalConExtras());
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

        itemPersonalizacionRepository.deleteByItemCarritoId(itemId);
        itemCarritoRepository.delete(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO crearGrupo(Long clienteId, String nombre, String nota) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        int maxOrden = carrito.getGrupos() != null
                ? carrito.getGrupos().stream().mapToInt(GrupoPedido::getOrden).max().orElse(0)
                : 0;

        GrupoPedido grupo = new GrupoPedido();
        grupo.setCarrito(carrito);
        grupo.setNombre(nombre != null ? nombre : "Pedido " + (maxOrden + 1));
        grupo.setNota(nota);
        grupo.setOrden(maxOrden + 1);
        grupoPedidoRepository.save(grupo);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO actualizarGrupo(Long clienteId, Long grupoId, String nombre, String nota) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        GrupoPedido grupo = grupoPedidoRepository.findById(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));

        if (!grupo.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El grupo no pertenece al carrito del cliente");
        }

        if (nombre != null) grupo.setNombre(nombre);
        if (nota != null) grupo.setNota(nota);
        grupoPedidoRepository.save(grupo);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO eliminarGrupo(Long clienteId, Long grupoId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        GrupoPedido grupo = grupoPedidoRepository.findById(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));

        if (!grupo.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El grupo no pertenece al carrito del cliente");
        }

        List<ItemCarrito> itemsDelGrupo = itemCarritoRepository.findByGrupoPedidoId(grupoId);
        for (ItemCarrito item : itemsDelGrupo) {
            item.setGrupoPedido(null);
            itemCarritoRepository.save(item);
        }

        grupoPedidoRepository.delete(grupo);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public CarritoResponseDTO moverItemAGrupo(Long clienteId, Long itemId, Long grupoId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del cliente");
        }

        GrupoPedido grupo = null;
        if (grupoId != null) {
            grupo = grupoPedidoRepository.findById(grupoId)
                    .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));
            if (!grupo.getCarrito().getId().equals(carrito.getId())) {
                throw new IllegalArgumentException("El grupo no pertenece al carrito del cliente");
            }
        }

        item.setGrupoPedido(grupo);
        itemCarritoRepository.save(item);

        Carrito actualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        return convertirADTO(actualizado);
    }

    @Transactional
    public void vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoActivo(clienteId);
        itemCarritoRepository.deleteAll(itemCarritoRepository.findByCarritoId(carrito.getId()));
        grupoPedidoRepository.deleteAll(grupoPedidoRepository.findByCarritoIdOrderByOrdenAsc(carrito.getId()));
    }

    @Transactional(readOnly = true)
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
                .detalles(carrito.getItems().stream().map(item -> {
                    GrupoPedido grupo = item.getGrupoPedido();

                    List<PedidoCheckoutRequestDTO.PersonalizacionCheckoutDTO> pers = List.of();
                    if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
                        pers = item.getPersonalizaciones().stream()
                                .map(p -> PedidoCheckoutRequestDTO.PersonalizacionCheckoutDTO.builder()
                                        .ingredienteId(p.getIngredienteId())
                                        .tipo(p.getTipo().name())
                                        .costoExtra(p.getCostoExtra())
                                        .nombreIngrediente(p.getNombreIngrediente())
                                        .build())
                                .collect(Collectors.toList());
                    }

                    return PedidoCheckoutRequestDTO.DetalleCheckoutDTO.builder()
                            .productoId(item.getProductoId())
                            .nombreProducto(item.getNombreProducto())
                            .cantidad(item.getCantidad())
                            .precioUnitario(item.getPrecioUnitario())
                            .subtotal(item.getSubtotal())
                            .grupoPedidoId(grupo != null ? grupo.getId() : null)
                            .grupoNombre(grupo != null ? grupo.getNombre() : null)
                            .grupoNota(grupo != null ? grupo.getNota() : null)
                            .personalizaciones(pers)
                            .build();
                }).collect(Collectors.toList()))
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
                        .map(this::convertirItemADTO)
                        .collect(Collectors.toList()) : List.of())
                .grupos(carrito.getGrupos() != null ? carrito.getGrupos().stream()
                        .map(grupo -> convertirGrupoADTO(grupo, carrito.getItems()))
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    private CarritoResponseDTO.ItemCarritoResponseDTO convertirItemADTO(ItemCarrito item) {
        GrupoPedido grupo = item.getGrupoPedido();
        return CarritoResponseDTO.ItemCarritoResponseDTO.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .nombreProducto(item.getNombreProducto())
                .imagenUrl(item.getImagenUrl())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .grupoPedidoId(grupo != null ? grupo.getId() : null)
                .grupoPedidoNombre(grupo != null ? grupo.getNombre() : null)
                .personalizaciones(item.getPersonalizaciones() != null
                        ? item.getPersonalizaciones().stream()
                                .map(this::convertirPersonalizacionADTO)
                                .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private CarritoResponseDTO.PersonalizacionResponseDTO convertirPersonalizacionADTO(ItemPersonalizacion p) {
        return CarritoResponseDTO.PersonalizacionResponseDTO.builder()
                .id(p.getId())
                .ingredienteId(p.getIngredienteId())
                .nombreIngrediente(p.getNombreIngrediente())
                .tipo(p.getTipo().name())
                .costoExtra(p.getCostoExtra())
                .build();
    }

    private CarritoResponseDTO.GrupoPedidoResponseDTO convertirGrupoADTO(GrupoPedido grupo, List<ItemCarrito> items) {
        List<ItemCarrito> itemsDelGrupo = items != null
                ? items.stream()
                        .filter(i -> i.getGrupoPedido() != null && i.getGrupoPedido().getId().equals(grupo.getId()))
                        .collect(Collectors.toList())
                : List.of();

        BigDecimal subtotalGrupo = itemsDelGrupo.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoResponseDTO.GrupoPedidoResponseDTO.builder()
                .id(grupo.getId())
                .nombre(grupo.getNombre())
                .nota(grupo.getNota())
                .orden(grupo.getOrden())
                .subtotal(subtotalGrupo)
                .items(itemsDelGrupo.stream()
                        .map(this::convertirItemADTO)
                        .collect(Collectors.toList()))
                .build();
    }
}