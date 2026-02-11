package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByClienteCorreo(String correo);

    // Consulta para sumar ingresos totales
    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.estado = 'COMPLETADA'")
    BigDecimal calcularIngresosTotales();

    // Consulta para obtener ranking de popularidad
    @Query("SELECT r.herramienta.nombre, COUNT(r) FROM Reserva r GROUP BY r.herramienta.nombre ORDER BY COUNT(r) DESC")
    List<Object[]> obtenerHerramientasMasPopulares();
}