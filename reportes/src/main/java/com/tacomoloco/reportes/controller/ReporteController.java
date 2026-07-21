package com.tacomoloco.reportes.controller;

import com.tacomoloco.reportes.dto.ReporteComprasDTO;
import com.tacomoloco.reportes.dto.ReporteInventarioDTO;
import com.tacomoloco.reportes.dto.ReporteVentasDTO;
import com.tacomoloco.reportes.service.PdfService;
import com.tacomoloco.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final PdfService pdfService;

    @GetMapping("/compras/mensual")
    public ResponseEntity<ReporteComprasDTO> reporteComprasMensual(
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(reporteService.generarReporteComprasMensual(year, month, usuarioId));
    }

    @GetMapping("/inventario")
    public ResponseEntity<ReporteInventarioDTO> reporteInventario(
            @RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(reporteService.generarReporteInventario(usuarioId));
    }

    @GetMapping("/ventas/top-productos")
    public ResponseEntity<ReporteVentasDTO> topProductos(
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(reporteService.generarTopProductos(year, month, usuarioId));
    }

    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> reporteVentasPdf(
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("X-User-Id") Long usuarioId) throws Exception {
        ReporteVentasDTO reporte = reporteService.generarTopProductos(year, month, usuarioId);
        byte[] pdf = pdfService.generarReporteVentasPdf(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "reporte-ventas-" + year + "-" + String.format("%02d", month) + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/inventario/pdf")
    public ResponseEntity<byte[]> reporteInventarioPdf(
            @RequestHeader("X-User-Id") Long usuarioId) throws Exception {
        ReporteInventarioDTO reporte = reporteService.generarReporteInventario(usuarioId);
        byte[] pdf = pdfService.generarReporteInventarioPdf(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "reporte-inventario-" + java.time.LocalDate.now() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}