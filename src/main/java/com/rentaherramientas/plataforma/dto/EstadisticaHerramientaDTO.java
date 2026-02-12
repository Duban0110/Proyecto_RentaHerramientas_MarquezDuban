package com.rentaherramientas.plataforma.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class EstadisticaHerramientaDTO {
    private String nombre;
    private Long cantidadReservas;

    // Escríbelo a mano para asegurar que Hibernate lo vea
    public EstadisticaHerramientaDTO(String nombre, Long cantidadReservas) {
        this.nombre = nombre;
        this.cantidadReservas = cantidadReservas;
    }
}