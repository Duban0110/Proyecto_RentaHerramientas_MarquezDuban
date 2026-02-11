package com.rentaherramientas.plataforma.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String correo;
    private String contrasena; // Usamos contrasena para ser iguales a tu Entidad
}