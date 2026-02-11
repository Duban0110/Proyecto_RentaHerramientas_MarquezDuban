package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data // Esto genera automáticamente el método setObservaciones
public class ReservaResponseDTO {
    private Long id;
    private String nombreCliente;
    private String nombreHerramienta;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal total;
    private String estado;

    // Estos campos deben existir para que el Service no falle
    private String observaciones;
    private BigDecimal cargosAdicionales;
}