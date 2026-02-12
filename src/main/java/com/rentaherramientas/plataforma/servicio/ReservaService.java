package com.rentaherramientas.plataforma.servicio;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rentaherramientas.plataforma.dto.*;
import com.rentaherramientas.plataforma.entidad.*;
import com.rentaherramientas.plataforma.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private PagoRepositorio pagoRepositorio; // Inyectado para la Opción B

    // --- MÉTODOS DE ESTADÍSTICAS ---

    public Double obtenerTotalIngresos() {
        // Sumamos directamente desde la tabla de pagos
        BigDecimal total = pagoRepositorio.sumarTodoLosPagos();
        return total != null ? total.doubleValue() : 0.0;
    }

    public List<EstadisticaHerramientaDTO> obtenerEstadisticasHerramientas() {
        return reservaRepositorio.obtenerHerramientasMasPopulares();
    }

    // --- GESTIÓN DE RESERVAS ---

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        // 1. Validaciones de existencia
        Herramienta herramienta = herramientaRepositorio.findById(dto.getHerramientaId())
                .orElseThrow(() -> new RuntimeException("Error: Herramienta no encontrada"));

        Usuario usuario = usuarioRepositorio.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado"));

        // 2. Lógica de Stock
        herramienta.reducirStock();
        herramientaRepositorio.saveAndFlush(herramienta);

        // 3. Crear la entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHerramienta(herramienta);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setEstado(EstadoReserva.ACTIVA);

        // 4. Guardar Reserva para obtener el ID
        Reserva reservaGuardada = reservaRepositorio.save(reserva);

        // 5. LÓGICA DE PAGO (Opción B)
        // Calcular el monto basándose en los días
        long dias = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin());
        if (dias <= 0) dias = 1;
        BigDecimal montoTotal = herramienta.getPrecioDia().multiply(BigDecimal.valueOf(dias));

        // Crear y guardar el registro de Pago
        Pago pago = new Pago();
        pago.setMonto(montoTotal);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("EFECTIVO/TRANSFERENCIA");
        pago.setReserva(reservaGuardada);
        pago.setNumeroFactura("FAC-" + System.currentTimeMillis());

        pagoRepositorio.save(pago); // Esto hace que el dashboard deje de marcar 0

        return convertirADTO(reservaGuardada);
    }

    public List<ReservaResponseDTO> listarPorCliente(Long id) {
        return reservaRepositorio.findByUsuarioId(id).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorCorreo(String correo) {
        return reservaRepositorio.findByUsuarioCorreo(correo).stream()
                .map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarParaProveedor(String correoProveedor) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getHerramienta() != null &&
                        r.getHerramienta().getProveedor().getCorreo().equalsIgnoreCase(correoProveedor))
                .map(this::convertirADTO).collect(Collectors.toList());
    }

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
        return "Error en la devolución.";
    }

    // --- GENERACIÓN DE FACTURA PDF ---

    public byte[] generarFacturaPDF(PagoRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // ESTO ES CLAVE: Mira la consola de IntelliJ cuando des clic al botón
        System.out.println("DEBUG - ID: " + reserva.getId());
        System.out.println("DEBUG - Estado en BD: [" + reserva.getEstado() + "]");
        System.out.println("DEBUG - ¿Es COMPLETADA?: " + (reserva.getEstado() == EstadoReserva.COMPLETADA));

        if (reserva.getEstado() == null || !reserva.getEstado().toString().equals("COMPLETADA")) {
            throw new RuntimeException("DEVOLUCIÓN PENDIENTE: No puede descargar la factura hasta devolver la herramienta.");
        }

        // Solo si es COMPLETADA llegará a este punto
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            // --- DISEÑO DEL PDF (Tu código actual) ---
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph titulo = new Paragraph("FACTURA DE ALQUILER\nRENTATOOLS", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            PdfPTable tablaInfo = new PdfPTable(2);
            tablaInfo.setWidthPercentage(100);
            tablaInfo.setSpacingBefore(10);

            agregarCeldaInfo(tablaInfo, "ID Reserva:", String.valueOf(reserva.getId()), fuenteSubtitulo, fuenteCuerpo);
            agregarCeldaInfo(tablaInfo, "Fecha Emisión:", java.time.LocalDate.now().toString(), fuenteSubtitulo, fuenteCuerpo);

            Usuario u = reserva.getUsuario();
            agregarCeldaInfo(tablaInfo, "Cliente:", u.getNombre() + " " + u.getApellido(), fuenteSubtitulo, fuenteCuerpo);
            agregarCeldaInfo(tablaInfo, "Correo:", u.getCorreo(), fuenteSubtitulo, fuenteCuerpo);

            documento.add(tablaInfo);

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
            // Usamos el monto que viene del DTO o el calculado de la reserva
            tablaDetalle.addCell(new Phrase("$" + dto.getMonto(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE)));

            documento.add(tablaDetalle);
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

    private ReservaResponseDTO convertirADTO(Reserva r) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(r.getId());
        dto.setNombreHerramienta(r.getHerramienta() != null ? r.getHerramienta().getNombre() : "N/A");

        if (r.getUsuario() != null) {
            dto.setNombreCliente(r.getUsuario().getNombre() + " " + r.getUsuario().getApellido());
        }

        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setEstado(r.getEstado() != null ? r.getEstado().name() : "ACTIVA");

        if (r.getHerramienta() != null && r.getFechaInicio() != null && r.getFechaFin() != null) {
            long dias = ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
            if (dias <= 0) dias = 1;

            BigDecimal precioPorDia = r.getHerramienta().getPrecioDia();
            dto.setTotal(precioPorDia.multiply(BigDecimal.valueOf(dias)));
        }

        return dto;
    }
}