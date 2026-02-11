package com.rentaherramientas.plataforma.dto;

import lombok.AllArgsConstructor; // OBLIGATORIO para el error que tienes
import lombok.NoArgsConstructor;  // OBLIGATORIO para que Spring funcione bien
import lombok.Data;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String mensaje;
    private String detalles;
}