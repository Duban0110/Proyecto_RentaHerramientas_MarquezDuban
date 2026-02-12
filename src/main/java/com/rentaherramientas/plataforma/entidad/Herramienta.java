package com.rentaherramientas.plataforma.entidad;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "herramientas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Herramienta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // BigDecimal es la mejor práctica para precios
    @Column(name = "precio_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioDia;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne(fetch = FetchType.EAGER) // Cambiado a EAGER para facilitar el mapeo al DTO
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;
}