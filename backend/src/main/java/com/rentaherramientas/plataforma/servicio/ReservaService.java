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
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        if (herramienta.getStock() <= 0) {
            throw new RuntimeException("No hay stock disponible");
        }

        Usuario cliente = usuarioRepositorio.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        herramienta.setStock(herramienta.getStock() - 1);
        herramientaRepositorio.save(herramienta);

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setHerramienta(herramienta);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());

        return convertirADTO(reservaRepositorio.save(reserva));
    }

    public List<ReservaResponseDTO> listarPorCorreo(String correo) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getCliente().getCorreo().equalsIgnoreCase(correo))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorCliente(Long id) {
        return reservaRepositorio.findAll().stream()
                .filter(r -> r.getCliente().getId().equals(id))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String completarDevolucion(DevolucionRequestDTO dto) {
        Reserva reserva = reservaRepositorio.findById(dto.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Herramienta h = reserva.getHerramienta();
        h.setStock(h.getStock() + 1);
        herramientaRepositorio.save(h);

        return "Devolución exitosa. Stock actualizado.";
    }

    public String procesarPagoSimulado(PagoRequestDTO dto) {
        return "RECIBO DE PAGO\nID Reserva: " + dto.getReservaId() + "\nEstado: PAGADO";
    }

    // CORRECCIÓN PARA BIGDECIMAL (Línea 111 aprox)
    public Double obtenerTotalIngresos() {
        List<Reserva> reservas = reservaRepositorio.findAll();
        return reservas.stream()
                .mapToDouble(r -> {
                    if (r.getHerramienta() == null || r.getHerramienta().getPrecioDia() == null) return 0.0;

                    long dias = ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
                    if (dias <= 0) dias = 1;

                    // Multiplicación correcta para BigDecimal
                    BigDecimal precio = r.getHerramienta().getPrecioDia();
                    BigDecimal total = precio.multiply(BigDecimal.valueOf(dias));

                    return total.doubleValue();
                }).sum();
    }

    // CORRECCIÓN PARA MAPAS (Línea 132 aprox)
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
        dto.setNombreHerramienta(r.getHerramienta().getNombre());
        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setEstado("ACTIVO");
        return dto;
    }
}