package com.rentaherramientas.plataforma.servicio;

import com.rentaherramientas.plataforma.dto.ReservaRequestDTO;
import com.rentaherramientas.plataforma.dto.ReservaResponseDTO;
import com.rentaherramientas.plataforma.entidad.*;
import com.rentaherramientas.plataforma.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        Usuario cliente = usuarioRepositorio.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Herramienta herramienta = herramientaRepositorio.findById(dto.getHerramientaId())
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        if (Boolean.FALSE.equals(herramienta.getDisponible())) {
            throw new RuntimeException("Lo sentimos, esta herramienta ya está alquilada.");
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setHerramienta(herramienta);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());

        long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        if (dias <= 0) dias = 1;
        BigDecimal totalCalculado = herramienta.getPrecioDia().multiply(new BigDecimal(dias));

        reserva.setTotal(totalCalculado);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        herramienta.setDisponible(false);
        herramientaRepositorio.save(herramienta);

        return convertirADTO(reservaRepositorio.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO completarDevolucion(Long reservaId) {
        Reserva reserva = reservaRepositorio.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.COMPLETADA);
        reserva.getHerramienta().setDisponible(true);

        herramientaRepositorio.save(reserva.getHerramienta());
        return convertirADTO(reservaRepositorio.save(reserva));
    }

    // UNIFICADO: Ahora se llama listarPorCorreo y usa el repo correcto
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorCorreo(String correo) {
        return reservaRepositorio.findByClienteCorreo(correo).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorCliente(Long clienteId) {
        return reservaRepositorio.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private ReservaResponseDTO convertirADTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        if (reserva.getCliente() != null) {
            dto.setNombreCliente(reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido());
        }
        if (reserva.getHerramienta() != null) {
            dto.setNombreHerramienta(reserva.getHerramienta().getNombre());
        }
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setTotal(reserva.getTotal());
        dto.setEstado(reserva.getEstado() != null ? reserva.getEstado().name() : null);
        return dto;
    }
}