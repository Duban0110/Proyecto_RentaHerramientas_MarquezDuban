package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Data // Genera automáticamente getUsuario() y setUsuario()
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Cambiado para coincidir con tu DB
    private Usuario usuario; // Cambiado de 'cliente' a 'usuario'

    @ManyToOne
    @JoinColumn(name = "herramienta_id", nullable = false)
    private Herramienta herramienta;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @Column(length = 500)
    private String observacionesDevolucion;

    private BigDecimal cargosAdicionales;
}