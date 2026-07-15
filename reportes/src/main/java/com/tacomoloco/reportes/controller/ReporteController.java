package com.tacomoloco.reportes.controller;

import com.tacomoloco.reportes.dto.ReporteComprasDTO;
import com.tacomoloco.reportes.dto.ReporteInventarioDTO;
import com.tacomoloco.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

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
}