package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerramientaRepositorio extends JpaRepository<Herramienta, Long> {
    // espacio para agg la busqueda por categoria
}