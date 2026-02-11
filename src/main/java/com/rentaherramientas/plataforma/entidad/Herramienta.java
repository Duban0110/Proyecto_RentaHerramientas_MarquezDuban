package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; // IMPORTANTE

@Entity
@Table(name = "herramientas")
@Data
public class Herramienta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioDia; // Cambiado a BigDecimal para coincidir con el DTO

    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;
}