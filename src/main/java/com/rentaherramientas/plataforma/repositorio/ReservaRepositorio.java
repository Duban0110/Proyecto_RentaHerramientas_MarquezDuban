package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import com.rentaherramientas.plataforma.dto.EstadisticaHerramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    // CORRECCIÓN: Cambiar 'Cliente' por 'Usuario' para que coincida con tu Entidad
    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByUsuarioCorreo(String correo);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM Reserva r")
    BigDecimal calcularIngresosTotales();

    @Query("SELECT r.herramienta.nombre AS herramienta, COUNT(r.id) AS cantidadReservas " +
            "FROM Reserva r " +
            "GROUP BY r.herramienta.nombre " +
            "ORDER BY COUNT(r.id) DESC")
    List<EstadisticaHerramienta> obtenerHerramientasMasPopulares();
}