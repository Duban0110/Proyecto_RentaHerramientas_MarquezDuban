package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long> {

    // COALESCE asegura que si no hay pagos, devuelva 0 en lugar de null
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p")
    BigDecimal sumarTodoLosPagos();
}