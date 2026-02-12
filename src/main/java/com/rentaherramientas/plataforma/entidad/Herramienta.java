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

    @Column(name = "precio_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioDia;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean disponible = true;

    // --- CAMPO ACTUALIZADO PARA SOPORTAR BASE64 PESADOS ---
    @Lob
    @Column(name = "imagen_url", columnDefinition = "LONGTEXT")
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Usuario proveedor;

    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    public void reducirStock() {
        if (this.stock > 0) {
            this.stock--;
            this.disponible = (this.stock > 0);
        } else {
            throw new RuntimeException("No hay stock suficiente para: " + this.nombre);
        }
    }

    public void aumentarStock() {
        this.stock++;
        this.disponible = true;
    }
}