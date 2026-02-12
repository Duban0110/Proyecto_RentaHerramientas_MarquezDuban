package com.rentaherramientas.plataforma.servicio;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rentaherramientas.plataforma.dto.*;
import com.rentaherramientas.plataforma.dto.EstadisticaHerramienta;
import com.rentaherramientas.plataforma.entidad.*;
import com.rentaherramientas.plataforma.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepositorio reservaRepositorio;

    @Autowired
    private HerramientaRepositorio herramientaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // --- MÉTODOS DE ESTADÍSTICAS ---
    public Double obtenerTotalIngresos() {
        BigDecimal total = reservaRepositorio.calcularIngresosTotales();
        return total != null ? total.doubleValue() : 0.0;
    }

    public List<EstadisticaHerramienta> obtenerEstadisticasHerramientas() {
        return reservaRepositorio.obtenerHerramientasMasPopulares();
    }

    // --- GESTIÓN DE RESERVAS ---
    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        Herramienta herramienta = herramientaRepositorio.findById(dto.getHerramientaId())
                .orElseThrow(() -> new RuntimeException("Error: Herramienta no encontrada"));

        Usuario cliente = usuarioRepositorio.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Error: Cliente no encontrado"));

        herramienta.reducirStock();
        herramientaRepositorio.saveAndFlush(herramienta);

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setHerramienta(herramienta);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return convertirADTO(reservaRepositorio.save(reserva));
    }

    /**
     * LÓGICA PARA GENERAR FACTURA EN PDF
     */
    public byte[] generarFacturaPDF(PagoRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            // Fuentes
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // Título
            Paragraph titulo = new Paragraph("FACTURA DE ALQUILER\nRENTATOOLS", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            // Información General en una tabla simple
            PdfPTable tablaInfo = new PdfPTable(2);
            tablaInfo.setWidthPercentage(100);
            tablaInfo.setSpacingBefore(10);

            agregarCeldaInfo(tablaInfo, "ID Reserva:", String.valueOf(reserva.getId()), fuenteSubtitulo, fuenteCuerpo);
            agregarCeldaInfo(tablaInfo, "Fecha Emisión:", java.time.LocalDate.now().toString(), fuenteSubtitulo, fuenteCuerpo);
            agregarCeldaInfo(tablaInfo, "Cliente:", reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido(), fuenteSubtitulo, fuenteCuerpo);
            agregarCeldaInfo(tablaInfo, "Correo:", reserva.getCliente().getCorreo(), fuenteSubtitulo, fuenteCuerpo);

            documento.add(tablaInfo);

            // Detalle del Alquiler
            Paragraph detalleHeader = new Paragraph("\nDETALLE DEL SERVICIO", fuenteSubtitulo);
            detalleHeader.setSpacingAfter(10);
            documento.add(detalleHeader);

            PdfPTable tablaDetalle = new PdfPTable(2);
            tablaDetalle.setWidthPercentage(100);

            tablaDetalle.addCell(new Phrase("Herramienta", fuenteSubtitulo));
            tablaDetalle.addCell(new Phrase(reserva.getHerramienta().getNombre(), fuenteCuerpo));

            tablaDetalle.addCell(new Phrase("Periodo", fuenteSubtitulo));
            tablaDetalle.addCell(new Phrase(reserva.getFechaInicio() + " al " + reserva.getFechaFin(), fuenteCuerpo));

            tablaDetalle.addCell(new Phrase("TOTAL PAGADO", fuenteSubtitulo));
            tablaDetalle.addCell(new Phrase("$" + dto.getMonto(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE)));

            documento.add(tablaDetalle);

            // Pie de página
            Paragraph pie = new Paragraph("\n\nEste documento es un comprobante oficial de pago simulao.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9));
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al construir el PDF: " + e.getMessage());
        }

        return salida.toByteArray();
    }

    private void agregarCeldaInfo(PdfPTable tabla, String label, String valor, Font fLabel, Font fValor) {
        PdfPCell celdaLabel = new PdfPCell(new Phrase(label, fLabel));
        celdaLabel.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celdaLabel);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fValor));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celdaValor);
    }

    // --- RESTO DE MÉTODOS (SIN CAMBIOS) ---

    @Transactional
    public String completarDevolucion(DevolucionRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        if (EstadoReserva.COMPLETADA.equals(reserva.getEstado())) {
            throw new RuntimeException("Esta reserva ya fue marcada como COMPLETADA.");
        }
        Herramienta h = reserva.getHerramienta();
        if (h != null) {
            h.aumentarStock();
            herramientaRepositorio.saveAndFlush(h);
            reserva.setEstado(EstadoReserva.COMPLETADA);
            reservaRepositorio.save(reserva);
            return "Devolución exitosa.";
        }
        return "Error.";
    }

    public List<ReservaResponseDTO> listarParaProveedor(String correoProveedor) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getHerramienta() != null &&
                        r.getHerramienta().getProveedor().getCorreo().equalsIgnoreCase(correoProveedor))
                .map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorCorreo(String correo) {
        return reservaRepositorio.findByClienteCorreo(correo).stream()
                .map(this::convertirADTO).collect(Collectors.toList());
    }

    public String procesarPagoSimulado(PagoRequestDTO dto) {
        return "Factura texto generada para reserva " + dto.getReservaId();
    }

    private ReservaResponseDTO convertirADTO(Reserva r) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(r.getId());
        dto.setNombreHerramienta(r.getHerramienta() != null ? r.getHerramienta().getNombre() : "N/A");

        if (r.getCliente() != null) {
            dto.setNombreCliente(r.getCliente().getNombre() + " " + r.getCliente().getApellido());
        }

        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setEstado(r.getEstado() != null ? r.getEstado().name() : "ACTIVA");

        // --- CÁLCULO DEL TOTAL SIN ERRORES ---
        if (r.getHerramienta() != null && r.getFechaInicio() != null && r.getFechaFin() != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());

            // Si se devuelve el mismo día, cobramos 1 día mínimo
            if (dias <= 0) dias = 1;

            // AQUÍ EL CAMBIO: Ya es BigDecimal, así que lo usamos directamente
            BigDecimal precioPorDia = r.getHerramienta().getPrecioDia();

            // Multiplicamos: precio * dias
            BigDecimal totalCalculado = precioPorDia.multiply(BigDecimal.valueOf(dias));

            dto.setTotal(totalCalculado);
        }

        return dto;
    }
}