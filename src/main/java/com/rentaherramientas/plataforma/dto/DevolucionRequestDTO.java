package com.rentaherramientas.plataforma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevolucionRequestDTO {
    private Long reservaId;
    private String observaciones;
    private BigDecimal cargosAdicionales;
}