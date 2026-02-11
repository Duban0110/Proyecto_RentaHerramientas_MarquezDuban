package com.rentaherramientas.plataforma.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    @Column(unique = true, nullable = false)
    private String correo;
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    // ESTO ES LO QUE FALTA Y CAUSA EL ERROR 500
    @OneToMany(mappedBy = "proveedor")
    @JsonIgnore // <--- Evita que Swagger entre en bucle infinito
    private List<Herramienta> herramientas;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore // <--- Evita que Swagger entre en bucle infinito
    private List<Reserva> reservas;
}