package com.rentaherramientas.plataforma.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {
    private Long clienteId;
    private Long herramientaId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}