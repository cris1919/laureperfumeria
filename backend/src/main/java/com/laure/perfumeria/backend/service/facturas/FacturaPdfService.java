package com.laure.perfumeria.backend.service.facturas;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta;
import com.laure.perfumeria.backend.model.ventas.Venta;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FacturaPdfService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String REGISTRADA = "REGISTRADA";

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Transactional(readOnly = true)
    public byte[] generar(Long idVenta, String correoAutenticado, boolean administrador) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la venta con ID " + idVenta));

        if (!administrador && !venta.getPedido().getCliente().getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new AccessDeniedException("No tienes permiso para descargar esta factura");
        }
        if (!REGISTRADA.equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalArgumentException("Solo se puede descargar la factura de una venta registrada");
        }

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font negrita = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL);

            Paragraph encabezado = new Paragraph("LAURE PERFUMERÍA", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);
            Paragraph factura = new Paragraph("FACTURA ELECTRÓNICA\nN.º F001-" + String.format("%08d", venta.getIdVenta()), negrita);
            factura.setAlignment(Element.ALIGN_CENTER);
            documento.add(factura);
            documento.add(new Paragraph("Fecha de emisión: " + venta.getFechaVenta().format(FORMATO_FECHA), normal));

            var cliente = venta.getPedido().getCliente();
            documento.add(new Paragraph("Cliente: " + cliente.getNombres() + " " + cliente.getApellidos(), normal));
            documento.add(new Paragraph("Correo: " + cliente.getCorreo(), normal));
            documento.add(new Paragraph("Dirección: " + (cliente.getDireccion() == null ? "-" : cliente.getDireccion()), normal));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(new float[]{4, 1, 1.5f, 1.5f});
            tabla.setWidthPercentage(100);
            agregarCelda(tabla, "Producto", negrita);
            agregarCelda(tabla, "Cant.", negrita);
            agregarCelda(tabla, "P. unitario", negrita);
            agregarCelda(tabla, "Subtotal", negrita);

            for (DetalleVenta detalle : detalleVentaRepository.findByVenta_IdVentaOrderByIdDetalleVentaAsc(idVenta)) {
                agregarCelda(tabla, detalle.getProducto().getNombre(), normal);
                agregarCelda(tabla, String.valueOf(detalle.getCantidad()), normal);
                agregarCelda(tabla, dinero(detalle.getPrecioUnitario()), normal);
                agregarCelda(tabla, dinero(detalle.getSubtotal()), normal);
            }
            documento.add(tabla);

            BigDecimal subtotal = venta.getPedido().getSubtotal();
            BigDecimal envio = venta.getPedido().getCostoEnvio();
            BigDecimal total = venta.getPedido().getTotal();
            documento.add(new Paragraph("Subtotal: " + dinero(subtotal), normal));
            documento.add(new Paragraph("Costo de envío: " + dinero(envio), normal));
            documento.add(new Paragraph("TOTAL: " + dinero(total), negrita));
            documento.add(new Paragraph("Documento generado electrónicamente por Laure Perfumería.", normal));
            documento.close();
            return salida.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar la factura PDF", exception);
        }
    }

    private void agregarCelda(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto == null ? "-" : texto, fuente));
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    private String dinero(BigDecimal valor) {
        return "S/ " + (valor == null ? "0.00" : valor.setScale(2).toPlainString());
    }
}
