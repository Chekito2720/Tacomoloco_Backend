package com.tacomoloco.catalogo.service;

import com.tacomoloco.catalogo.client.IngredienteClient;
import com.tacomoloco.catalogo.dto.IngredienteDTO;
import com.tacomoloco.catalogo.dto.ProductoRequestDTO;
import com.tacomoloco.catalogo.dto.ProductoResponseDTO;
import com.tacomoloco.catalogo.entity.Producto;
import com.tacomoloco.catalogo.entity.ProductoIngrediente;
import com.tacomoloco.catalogo.repository.ProductoIngredienteRepository;
import com.tacomoloco.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final IngredienteClient ingredienteClient;

    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        Producto producto = new Producto();
        mapearProducto(producto, request);
        producto = productoRepository.save(producto);
        guardarIngredientes(producto, request.getIngredientes());
        return convertirADTO(producto);
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarDisponibles() {
        return productoRepository.findByDisponibleTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        mapearProducto(producto, request);
        productoIngredienteRepository.deleteByProductoId(id);
        guardarIngredientes(producto, request.getIngredientes());

        producto = productoRepository.save(producto);
        return convertirADTO(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoIngredienteRepository.deleteByProductoId(id);
        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO.IngredienteResponseDTO> obtenerIngredientes(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        if (producto.getIngredientes() == null) {
            return List.of();
        }

        return producto.getIngredientes().stream()
                .map(pi -> {
                    IngredienteDTO ing = ingredienteClient.getIngredienteById(pi.getIngredienteId());
                    ProductoResponseDTO.IngredienteResponseDTO ingDTO = new ProductoResponseDTO.IngredienteResponseDTO();
                    ingDTO.setIngredienteId(pi.getIngredienteId());
                    ingDTO.setNombreIngrediente(ing != null ? ing.getNombre() : "N/A");
                    ingDTO.setUnidadMedida(ing != null ? ing.getUnidadMedida() : "N/A");
                    ingDTO.setCantidadRequerida(pi.getCantidadRequerida());
                    ingDTO.setCostoUnitario(ing != null ? ing.getCostoUnitario() : BigDecimal.ZERO);
                    return ingDTO;
                })
                .collect(Collectors.toList());
    }

    private void mapearProducto(Producto producto, ProductoRequestDTO request) {
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setDisponible(request.getDisponible() != null ? request.getDisponible() : true);
        producto.setCategoria(request.getCategoria());
    }

    private void guardarIngredientes(Producto producto, List<ProductoRequestDTO.IngredienteRequestDTO> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return;
        }

        for (ProductoRequestDTO.IngredienteRequestDTO ingReq : ingredientes) {
            IngredienteDTO ingrediente = ingredienteClient.getIngredienteById(ingReq.getIngredienteId());
            if (ingrediente == null || !ingrediente.getActivo()) {
                throw new IllegalArgumentException("Ingrediente no encontrado o inactivo: " + ingReq.getIngredienteId());
            }

            ProductoIngrediente pi = new ProductoIngrediente();
            pi.setProducto(producto);
            pi.setIngredienteId(ingReq.getIngredienteId());
            pi.setCantidadRequerida(ingReq.getCantidadRequerida());
            productoIngredienteRepository.save(pi);
        }
    }

    private ProductoResponseDTO convertirADTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setDisponible(producto.getDisponible());
        dto.setCategoria(producto.getCategoria());

        if (producto.getIngredientes() != null) {
            dto.setIngredientes(producto.getIngredientes().stream()
                    .map(pi -> {
                        IngredienteDTO ing = ingredienteClient.getIngredienteById(pi.getIngredienteId());
                        ProductoResponseDTO.IngredienteResponseDTO ingDTO = new ProductoResponseDTO.IngredienteResponseDTO();
                        ingDTO.setIngredienteId(pi.getIngredienteId());
                        ingDTO.setNombreIngrediente(ing != null ? ing.getNombre() : "N/A");
                        ingDTO.setUnidadMedida(ing != null ? ing.getUnidadMedida() : "N/A");
                        ingDTO.setCantidadRequerida(pi.getCantidadRequerida());
                        ingDTO.setCostoUnitario(ing != null ? ing.getCostoUnitario() : BigDecimal.ZERO);
                        return ingDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}