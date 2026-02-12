package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Data // Esto genera automáticamente los Getters y Setters
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "herramienta_id", nullable = false)
    private Herramienta herramienta;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    // --- NUEVOS CAMPOS PARA CUMPLIR REQUERIMIENTOS ---
    @Column(length = 500)
    private String observacionesDevolucion;

    private BigDecimal cargosAdicionales;

    }