package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequestDTO {
    private Long reservaId;
    private String numeroTarjeta;
    private String cvv;
    private String nombreTitular;
    private BigDecimal monto; // <--- AGREGA ESTA LÍNEA
}