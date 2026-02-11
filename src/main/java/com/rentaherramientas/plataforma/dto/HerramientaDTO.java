package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.math.BigDecimal; // Asegúrate de usar BigDecimal

@Data
public class HerramientaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioDia; // Cambiado a BigDecimal
    private Integer stock;
    private boolean disponible;
    private String nombreProveedor;
}