package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "herramientas")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Herramienta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(name = "precio_dia")
    private BigDecimal precioDia;

    private boolean disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;
}