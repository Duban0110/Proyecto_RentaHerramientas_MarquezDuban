package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerramientaRepositorio extends JpaRepository<Herramienta, Long> {
    // Aquí luego podremos agregar búsquedas por categoría o disponibilidad
}