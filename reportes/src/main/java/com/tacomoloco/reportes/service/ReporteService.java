package com.tacomoloco.reportes.service;

import com.tacomoloco.reportes.client.InventarioClient;
import com.tacomoloco.reportes.client.PedidoClient;
import com.tacomoloco.reportes.dto.DetallePedidoDTO;
import com.tacomoloco.reportes.dto.IngredienteDTO;
import com.tacomoloco.reportes.dto.PedidoDTO;
import com.tacomoloco.reportes.dto.ReporteComprasDTO;
import com.tacomoloco.reportes.dto.ReporteInventarioDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

    private final PedidoClient pedidoClient;
    private final InventarioClient inventarioClient;
    private final VentaResumenRepository ventaResumenRepository;
    private final ReporteGeneradoRepository reporteGeneradoRepository;

    @Transactional(readOnly = true)
    public ReporteComprasDTO generarReporteComprasMensual(int year, int month, Long usuarioId) {
        LocalDateTime inicio = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1).minusNanos(1);

        List<PedidoDTO> pedidos = pedidoClient.getPedidosByFechaBetween(inicio, fin);

        BigDecimal totalCompras = pedidos.stream()
                .filter(p -> "ENTREGADO".equals(p.getEstado()))
                .map(PedidoDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, Integer> productoCantidad = pedidos.stream()
                .filter(p -> "ENTREGADO".equals(p.getEstado()))
                .flatMap(p -> p.getDetalles().stream())
                .collect(Collectors.groupingBy(
                        DetallePedidoDTO::getProductoId,
                        Collectors.summingInt(DetallePedidoDTO::getCantidad)
                ));

        List<ReporteComprasDTO.ResumenProductoDTO> productosMasVendidos = productoCantidad.entrySet().stream()
                .map(e -> {
                    ReporteComprasDTO.ResumenProductoDTO dto = new ReporteComprasDTO.ResumenProductoDTO();
                    dto.setProductoId(e.getKey());
                    dto.setProductoNombre("Producto " + e.getKey());
                    dto.setCantidadTotal(e.getValue());
                    dto.setTotalVendido(BigDecimal.ZERO);
                    return dto;
                })
                .sorted(Comparator.comparing(ReporteComprasDTO.ResumenProductoDTO::getCantidadTotal).reversed())
                .limit(10)
                .collect(Collectors.toList());

        ReporteComprasDTO reporte = new ReporteComprasDTO();
        reporte.setFechaInicio(inicio.toLocalDate());
        reporte.setFechaFin(fin.toLocalDate());
        reporte.setTotalCompras(totalCompras);
        reporte.setTotalPedidos((int) pedidos.stream().filter(p -> "ENTREGADO".equals(p.getEstado())).count());
        reporte.setProductosMasVendidos(productosMasVendidos);
        reporte.setIngredientesConsumidos(List.of());

        guardarRegistroReporte(usuarioId, ReporteGenerado.TipoReporte.COMPRAS, year + "-" + month);
        return reporte;
    }

    @Transactional(readOnly = true)
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

    private void guardarRegistroReporte(Long usuarioId, ReporteGenerado.TipoReporte tipo, String parametros) {
        ReporteGenerado registro = new ReporteGenerado();
        registro.setTipo(tipo);
        registro.setParametros(parametros);
        registro.setGeneradoPor(usuarioId);
        reporteGeneradoRepository.save(registro);
    }
}