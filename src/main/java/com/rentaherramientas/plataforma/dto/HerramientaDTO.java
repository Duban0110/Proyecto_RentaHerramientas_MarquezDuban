package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HerramientaDTO {
    private Long id;
    private String nombre;
    private String descripcion; // <--- ESTA ES LA LÍNEA QUE FALTABA
    private BigDecimal precioDia;
    private Boolean disponible;
    private String nombreProveedor;
}