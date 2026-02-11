package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepositorio extends JpaRepository<Pago, Long> {
}