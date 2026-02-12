package com.rentaherramientas.plataforma.servicio;

import com.rentaherramientas.plataforma.dto.*;
import com.rentaherramientas.plataforma.entidad.*;
import com.rentaherramientas.plataforma.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepositorio reservaRepositorio;
    @Autowired
    private HerramientaRepositorio herramientaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        Herramienta herramienta = herramientaRepositorio.findById(dto.getHerramientaId())
                .orElseThrow(() -> new RuntimeException("Error: Herramienta ID " + dto.getHerramientaId() + " no encontrada"));

        if (herramienta.getStock() <= 0) {
            throw new RuntimeException("Lo sentimos, no queda stock disponible para: " + herramienta.getNombre());
        }

        Usuario cliente = usuarioRepositorio.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Error: Cliente ID " + dto.getClienteId() + " no encontrado"));

        // Lógica de descuento
        herramienta.setStock(herramienta.getStock() - 1);
        herramientaRepositorio.saveAndFlush(herramienta);

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setHerramienta(herramienta);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());

        Reserva reservaGuardada = reservaRepositorio.save(reserva);
        return convertirADTO(reservaGuardada);
    }

    public List<ReservaResponseDTO> listarPorCliente(Long id) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getCliente() != null && r.getCliente().getId().equals(id))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorCorreo(String correo) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getCliente() != null && r.getCliente().getCorreo().equalsIgnoreCase(correo))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String completarDevolucion(DevolucionRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Herramienta h = reserva.getHerramienta();
        if (h != null) {
            h.setStock(h.getStock() + 1);
            herramientaRepositorio.saveAndFlush(h);
            return "Devolución exitosa. Stock de " + h.getNombre() + " actualizado.";
        }
        return "Error: No se encontró la herramienta vinculada a la reserva.";
    }

    public String procesarPagoSimulado(PagoRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada para facturación"));

        return "==========================================\n" +
                "       FACTURA DE ALQUILER - RENTATOOLS   \n" +
                "==========================================\n" +
                "ID Reserva: " + reserva.getId() + "\n" +
                "Cliente: " + reserva.getCliente().getNombre() + "\n" +
                "Herramienta: " + reserva.getHerramienta().getNombre() + "\n" +
                "Monto: $" + dto.getMonto() + "\n" +
                "Estado: PAGADO (Simulado)\n" +
                "==========================================\n";
    }

    public Double obtenerTotalIngresos() {
        return reservaRepositorio.findAll().stream()
                .mapToDouble(r -> {
                    if (r.getHerramienta() == null || r.getHerramienta().getPrecioDia() == null) return 0.0;
                    long dias = ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
                    if (dias <= 0) dias = 1;
                    return r.getHerramienta().getPrecioDia().multiply(BigDecimal.valueOf(dias)).doubleValue();
                }).sum();
    }

    // --- ESTE ES EL MÉTODO QUE HACÍA FALTA PARA EL ADMINCONTROLADOR ---
    public List<Map<String, Object>> obtenerEstadisticasHerramientas() {
        List<Reserva> todas = reservaRepositorio.findAll();
        Map<String, Long> conteo = todas.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getHerramienta() != null ? r.getHerramienta().getNombre() : "Desconocida",
                        Collectors.counting()
                ));

        return conteo.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("herramienta", entry.getKey());
                    map.put("cantidadReservas", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private ReservaResponseDTO convertirADTO(Reserva r) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(r.getId());
        dto.setNombreHerramienta(r.getHerramienta() != null ? r.getHerramienta().getNombre() : "Herramienta eliminada");
        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setEstado("ACTIVO");
        return dto;
    }
}