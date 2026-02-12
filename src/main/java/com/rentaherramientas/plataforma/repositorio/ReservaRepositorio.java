package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import com.rentaherramientas.plataforma.dto.EstadisticaHerramienta; // Importa la interfaz
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByClienteCorreo(String correo);

    // Usamos COALESCE para que si la suma es nula, devuelva 0
    @Query("SELECT COALESCE(SUM(r.total), 0) FROM Reserva r")
    BigDecimal calcularIngresosTotales();

    // Consulta con Proyección: Los alias (AS herramienta) deben coincidir con los getters de la interfaz
    @Query("SELECT r.herramienta.nombre AS herramienta, COUNT(r) AS cantidadReservas " +
            "FROM Reserva r " +
            "GROUP BY r.herramienta.nombre " +
            "ORDER BY COUNT(r) DESC")
    List<EstadisticaHerramienta> obtenerHerramientasMasPopulares();
}