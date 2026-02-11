package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // Útil para crear objetos rápidamente en las pruebas
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    // Usamos precision y scale para manejar dinero correctamente (10 dígitos, 2 decimales)
    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    // Relación con el Usuario (quien alquila)
    // El nombre de la columna en MySQL será 'cliente_id'
    @ManyToOne(fetch = FetchType.LAZY) // EAGER para que cargue los datos del cliente al buscar la reserva
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    // Relación con la Herramienta (qué se alquila)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "herramienta_id", nullable = false)
    private Herramienta herramienta;
}