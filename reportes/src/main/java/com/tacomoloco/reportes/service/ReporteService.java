package com.tacomoloco.reportes.service;

import com.tacomoloco.reportes.client.CatalogoClient;
import com.tacomoloco.reportes.client.InventarioClient;
import com.tacomoloco.reportes.client.PedidoClient;
import com.tacomoloco.reportes.dto.*;
import com.tacomoloco.reportes.entity.ReporteGenerado;
import com.tacomoloco.reportes.entity.VentaResumen;
import com.tacomoloco.reportes.repository.ReporteGeneradoRepository;
import com.tacomoloco.reportes.repository.VentaResumenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

    private final PedidoClient pedidoClient;
    private final InventarioClient inventarioClient;
    private final CatalogoClient catalogoClient;
    private final VentaResumenRepository ventaResumenRepository;
    private final ReporteGeneradoRepository reporteGeneradoRepository;

    @Transactional
    public ReporteComprasDTO generarReporteComprasMensual(int year, int month, Long usuarioId) {
        LocalDateTime inicio = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1).minusNanos(1);

        List<PedidoDTO> pedidos = pedidoClient.getPedidosByFechaBetween(inicio, fin);

        List<PedidoDTO> entregados = pedidos.stream()
                .filter(p -> "ENTREGADO".equals(p.getEstado()))
                .collect(Collectors.toList());

        BigDecimal totalCompras = entregados.stream()
                .map(PedidoDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, Integer> productoCantidad = entregados.stream()
                .flatMap(p -> nullSafeDetalles(p).stream())
                .collect(Collectors.groupingBy(
                        DetallePedidoDTO::getProductoId,
                        Collectors.summingInt(DetallePedidoDTO::getCantidad)
                ));

        Map<Long, BigDecimal> productoTotal = entregados.stream()
                .flatMap(p -> nullSafeDetalles(p).stream())
                .collect(Collectors.groupingBy(
                        DetallePedidoDTO::getProductoId,
                        Collectors.reducing(BigDecimal.ZERO, DetallePedidoDTO::getSubtotal, BigDecimal::add)
                ));

        List<ReporteComprasDTO.ResumenProductoDTO> productosMasVendidos = productoCantidad.entrySet().stream()
                .map(e -> {
                    ReporteComprasDTO.ResumenProductoDTO dto = new ReporteComprasDTO.ResumenProductoDTO();
                    dto.setProductoId(e.getKey());
                    dto.setProductoNombre(obtenerNombreProducto(e.getKey()));
                    dto.setCantidadTotal(e.getValue());
                    dto.setTotalVendido(productoTotal.getOrDefault(e.getKey(), BigDecimal.ZERO));
                    return dto;
                })
                .sorted(Comparator.comparing(ReporteComprasDTO.ResumenProductoDTO::getCantidadTotal).reversed())
                .limit(5)
                .collect(Collectors.toList());

        ReporteComprasDTO reporte = new ReporteComprasDTO();
        reporte.setFechaInicio(inicio.toLocalDate());
        reporte.setFechaFin(fin.toLocalDate());
        reporte.setTotalCompras(totalCompras);
        reporte.setTotalPedidos(entregados.size());
        reporte.setProductosMasVendidos(productosMasVendidos);
        reporte.setIngredientesConsumidos(List.of());

        guardarRegistroReporte(usuarioId, ReporteGenerado.TipoReporte.COMPRAS, year + "-" + month);
        return reporte;
    }

    @Transactional
    public ReporteInventarioDTO generarReporteInventario(Long usuarioId) {
        List<IngredienteDTO> ingredientes = inventarioClient.getAllIngredientes();

        List<ReporteInventarioDTO.IngredienteStockDTO> detalle = ingredientes.stream().map(ing -> {
            ReporteInventarioDTO.IngredienteStockDTO dto = new ReporteInventarioDTO.IngredienteStockDTO();
            dto.setId(ing.getId());
            dto.setNombre(ing.getNombre());
            dto.setUnidadMedida(ing.getUnidadMedida());
            dto.setCantidadDisponible(ing.getCantidadDisponible());
            dto.setStockMinimo(ing.getStockMinimo());
            dto.setEstado(ing.getEstado());
            dto.setCostoUnitario(ing.getCostoUnitario());
            dto.setValorTotal(ing.getCostoUnitario().multiply(BigDecimal.valueOf(ing.getCantidadDisponible())));
            dto.setAlertaStockBajo("STOCK_BAJO".equals(ing.getEstado()) || "AGOTADO".equals(ing.getEstado()));
            return dto;
        }).collect(Collectors.toList());

        long disponibles = detalle.stream().filter(d -> "DISPONIBLE".equals(d.getEstado())).count();
        long stockBajo = detalle.stream().filter(d -> "STOCK_BAJO".equals(d.getEstado())).count();
        long agotados = detalle.stream().filter(d -> "AGOTADO".equals(d.getEstado())).count();
        BigDecimal valorTotal = detalle.stream()
                .map(ReporteInventarioDTO.IngredienteStockDTO::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReporteInventarioDTO reporte = new ReporteInventarioDTO();
        reporte.setTotalIngredientes(ingredientes.size());
        reporte.setDisponibles((int) disponibles);
        reporte.setStockBajo((int) stockBajo);
        reporte.setAgotados((int) agotados);
        reporte.setValorTotalInventario(valorTotal);
        reporte.setDetalle(detalle);

        guardarRegistroReporte(usuarioId, ReporteGenerado.TipoReporte.INGREDIENTES, "inventario-" + LocalDate.now());
        return reporte;
    }

    @Transactional
    public ReporteVentasDTO generarTopProductos(int year, int month, Long usuarioId) {
        LocalDateTime inicio = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1).minusNanos(1);

        List<PedidoDTO> pedidos = pedidoClient.getPedidosByFechaBetween(inicio, fin);

        List<PedidoDTO> entregados = pedidos.stream()
                .filter(p -> "ENTREGADO".equals(p.getEstado()))
                .collect(Collectors.toList());

        BigDecimal totalCompras = entregados.stream()
                .map(PedidoDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, Integer> productoCantidad = entregados.stream()
                .flatMap(p -> nullSafeDetalles(p).stream())
                .collect(Collectors.groupingBy(
                        DetallePedidoDTO::getProductoId,
                        Collectors.summingInt(DetallePedidoDTO::getCantidad)
                ));

        Map<Long, BigDecimal> productoTotal = entregados.stream()
                .flatMap(p -> nullSafeDetalles(p).stream())
                .collect(Collectors.groupingBy(
                        DetallePedidoDTO::getProductoId,
                        Collectors.reducing(BigDecimal.ZERO, DetallePedidoDTO::getSubtotal, BigDecimal::add)
                ));

        int[] posicion = {1};
        List<ReporteVentasDTO.ProductoTopDTO> topProductos = productoCantidad.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    String nombre = obtenerNombreProducto(e.getKey());
                    String categoria = obtenerCategoriaProducto(e.getKey());

                    VentaResumen venta = new VentaResumen();
                    venta.setFecha(inicio.toLocalDate());
                    venta.setProductoId(e.getKey());
                    venta.setCantidadVendida(e.getValue());
                    venta.setTotalVenta(productoTotal.getOrDefault(e.getKey(), BigDecimal.ZERO));
                    ventaResumenRepository.save(venta);

                    return ReporteVentasDTO.ProductoTopDTO.builder()
                            .productoId(e.getKey())
                            .productoNombre(nombre)
                            .categoria(categoria)
                            .cantidadTotal(e.getValue())
                            .totalVendido(productoTotal.getOrDefault(e.getKey(), BigDecimal.ZERO))
                            .posicion(posicion[0]++)
                            .build();
                })
                .collect(Collectors.toList());

        guardarRegistroReporte(usuarioId, ReporteGenerado.TipoReporte.VENTAS, year + "-" + month);

        return ReporteVentasDTO.builder()
                .periodo(year + "-" + String.format("%02d", month))
                .totalCompras(totalCompras)
                .totalPedidos(entregados.size())
                .topProductos(topProductos)
                .build();
    }

    private java.util.List<DetallePedidoDTO> nullSafeDetalles(PedidoDTO p) {
        return p.getDetalles() != null ? p.getDetalles() : java.util.List.of();
    }

    private String obtenerNombreProducto(Long productoId) {
        try {
            ProductoCatalogoDTO producto = catalogoClient.getProductoById(productoId);
            return producto != null ? producto.getNombre() : "Producto " + productoId;
        } catch (Exception e) {
            log.warn("No se pudo obtener el producto {} del catalogo: {}", productoId, e.getMessage());
            return "Producto " + productoId;
        }
    }

    private String obtenerCategoriaProducto(Long productoId) {
        try {
            ProductoCatalogoDTO producto = catalogoClient.getProductoById(productoId);
            return producto != null ? producto.getCategoria() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Transactional
    private void guardarRegistroReporte(Long usuarioId, ReporteGenerado.TipoReporte tipo, String parametros) {
        ReporteGenerado registro = new ReporteGenerado();
        registro.setTipo(tipo);
        registro.setParametros(parametros);
        registro.setGeneradoPor(usuarioId);
        reporteGeneradoRepository.save(registro);
    }
}