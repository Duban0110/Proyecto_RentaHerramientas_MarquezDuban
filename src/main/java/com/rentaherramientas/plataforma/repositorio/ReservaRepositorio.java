package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
    // Busca en la relación 'cliente' el atributo 'correo'
    List<Reserva> findByClienteCorreo(String correo);

    // Busca por el ID del cliente
    List<Reserva> findByClienteId(Long clienteId);
}