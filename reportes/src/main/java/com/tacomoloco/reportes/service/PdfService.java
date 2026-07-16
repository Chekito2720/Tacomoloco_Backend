package com.tacomoloco.reportes.service;

import com.tacomoloco.reportes.dto.ReporteComprasDTO;
import com.tacomoloco.reportes.dto.ReporteInventarioDTO;
import com.tacomoloco.reportes.dto.ReporteVentasDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PdfService {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    public byte[] generarReporteVentasPdf(ReporteVentasDTO reporte) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                y = drawTitle(content, "Reporte de Ventas", y);
                y = drawSubtitle(content, "Periodo: " + reporte.getPeriodo(), y - 25);

                y -= 30;
                y = drawLabelValue(content, "Total de Compras:", "$" + reporte.getTotalCompras(), y);
                y = drawLabelValue(content, "Pedidos Entregados:", String.valueOf(reporte.getTotalPedidos()), y);

                y -= 30;
                y = drawSectionHeader(content, "TOP 5 PRODUCTOS MAS VENDIDOS", y);

                y -= 25;
                y = drawTableHeader(content, new String[]{"#", "Producto", "Categoria", "Cant.", "Total"}, y);

                if (reporte.getTopProductos() != null) {
                    for (ReporteVentasDTO.ProductoTopDTO p : reporte.getTopProductos()) {
                        y -= 20;
                        if (y < MARGIN + 50) {
                            content.close();
                            PDPage newPage = new PDPage(PDRectangle.A4);
                            document.addPage(newPage);
                            y = PAGE_HEIGHT - MARGIN;
                        }
                        y = drawTableRowPdfBox(content, new String[]{
                                String.valueOf(p.getPosicion()),
                                p.getProductoNombre(),
                                p.getCategoria(),
                                String.valueOf(p.getCantidadTotal()),
                                "$" + p.getTotalVendido()
                        }, y);
                    }
                }
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    public byte[] generarReporteInventarioPdf(ReporteInventarioDTO reporte) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                y = drawTitle(content, "Reporte de Inventario", y);
                y = drawSubtitle(content, "Fecha: " + java.time.LocalDate.now(), y - 25);

                y -= 30;
                y = drawLabelValue(content, "Total Ingredientes:", String.valueOf(reporte.getTotalIngredientes()), y);
                y = drawLabelValue(content, "Disponibles:", String.valueOf(reporte.getDisponibles()), y);
                y = drawLabelValue(content, "Stock Bajo:", String.valueOf(reporte.getStockBajo()), y);
                y = drawLabelValue(content, "Agotados:", String.valueOf(reporte.getAgotados()), y);
                y = drawLabelValue(content, "Valor Total Inventario:", "$" + reporte.getValorTotalInventario(), y);

                y -= 30;
                y = drawSectionHeader(content, "DETALLE DE INGREDIENTES", y);

                y -= 25;
                y = drawTableHeader(content, new String[]{"Ingrediente", "Estado", "Cant. Disp.", "Costo Unit.", "Valor Total"}, y);

                if (reporte.getDetalle() != null) {
                    for (ReporteInventarioDTO.IngredienteStockDTO ing : reporte.getDetalle()) {
                        y -= 20;
                        if (y < MARGIN + 50) {
                            content.close();
                            PDPage newPage = new PDPage(PDRectangle.A4);
                            document.addPage(newPage);
                            y = PAGE_HEIGHT - MARGIN;
                        }
                        y = drawTableRowPdfBox(content, new String[]{
                                ing.getNombre(),
                                ing.getEstado(),
                                String.valueOf(ing.getCantidadDisponible()),
                                "$" + ing.getCostoUnitario(),
                                "$" + ing.getValorTotal()
                        }, y);

                        if (Boolean.TRUE.equals(ing.getAlertaStockBajo())) {
                            y = drawWarning(content, "  ** ALERTA: Stock bajo **", y - 15);
                        }
                    }
                }
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    private float drawTitle(PDPageContentStream content, String text, float y) throws IOException {
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        content.newLineAtOffset(MARGIN, y);
        content.showText(text);
        content.endText();
        return y - 25;
    }

    private float drawSubtitle(PDPageContentStream content, String text, float y) throws IOException {
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        content.newLineAtOffset(MARGIN, y);
        content.showText(text);
        content.endText();
        return y;
    }

    private float drawSectionHeader(PDPageContentStream content, String text, float y) throws IOException {
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
        content.newLineAtOffset(MARGIN, y);
        content.showText(text);
        content.endText();
        return y;
    }

    private float drawLabelValue(PDPageContentStream content, String label, String value, float y) throws IOException {
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
        content.newLineAtOffset(MARGIN, y);
        content.showText(label);
        content.endText();

        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        content.newLineAtOffset(MARGIN + 200, y);
        content.showText(value);
        content.endText();
        return y - 20;
    }

    private float drawTableHeader(PDPageContentStream content, String[] headers, float y) throws IOException {
        float[] colWidths = {30, 160, 100, 50, 80};
        float x = MARGIN;

        content.setLineWidth(0.5f);
        content.moveTo(MARGIN, y + 5);
        content.lineTo(PAGE_WIDTH - MARGIN, y + 5);
        content.stroke();

        for (int i = 0; i < headers.length; i++) {
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
            content.newLineAtOffset(x, y);
            content.showText(headers[i]);
            content.endText();
            x += colWidths[i];
        }

        content.moveTo(MARGIN, y - 5);
        content.lineTo(PAGE_WIDTH - MARGIN, y - 5);
        content.stroke();

        return y;
    }

    private float drawTableRowPdfBox(PDPageContentStream content, String[] cells, float y) throws IOException {
        float[] colWidths = {30, 160, 100, 50, 80};
        float x = MARGIN;

        for (int i = 0; i < cells.length; i++) {
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            content.newLineAtOffset(x, y);
            content.showText(cells[i]);
            content.endText();
            x += colWidths[i];
        }
        return y;
    }

    private float drawWarning(PDPageContentStream content, String text, float y) throws IOException {
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE), 8);
        content.newLineAtOffset(MARGIN + 10, y);
        content.showText(text);
        content.endText();
        return y - 15;
    }
}