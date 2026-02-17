package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HerramientaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioDia;
    private Integer stock;
    private Boolean disponible;
    private String imagenUrl;
    private String nombreProveedor;
}