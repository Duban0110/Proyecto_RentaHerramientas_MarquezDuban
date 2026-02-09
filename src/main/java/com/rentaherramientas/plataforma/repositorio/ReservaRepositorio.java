package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
}
