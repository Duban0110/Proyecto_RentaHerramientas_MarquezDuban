package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Reserva;
import com.rentaherramientas.plataforma.dto.EstadisticaHerramientaDTO; // 1. Import corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByUsuarioCorreo(String correo);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM Reserva r")
    BigDecimal calcularIngresosTotales();

    // 2. Usamos el nombre exacto de la clase en el constructor (new ...DTO)
    // 3. El retorno debe ser List<EstadisticaHerramientaDTO>
    @Query("SELECT new com.rentaherramientas.plataforma.dto.EstadisticaHerramientaDTO(r.herramienta.nombre, COUNT(r.id)) " +
            "FROM Reserva r " +
            "GROUP BY r.herramienta.nombre " +
            "ORDER BY COUNT(r.id) DESC")
    List<EstadisticaHerramientaDTO> obtenerHerramientasMasPopulares();
}