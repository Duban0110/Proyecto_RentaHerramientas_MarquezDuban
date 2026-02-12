package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.servicio.ReservaService;
import com.rentaherramientas.plataforma.dto.EstadisticaHerramienta;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
@PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'ROLE_ADMINISTRADOR')")
public class AdminControlador {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/resumen")
    @Operation(summary = "Dashboard administrativo: Ingresos y estadísticas de uso")
    public ResponseEntity<Map<String, Object>> obtenerResumenCompleto() {
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("totalIngresos", reservaService.obtenerTotalIngresos());
        respuesta.put("estadisticasHerramientas", reservaService.obtenerEstadisticasHerramientas());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<Double> verIngresos() {
        return ResponseEntity.ok(reservaService.obtenerTotalIngresos());
    }

    @GetMapping("/herramientas-populares")
    public ResponseEntity<?> verEstadisticas() {
        return ResponseEntity.ok(reservaService.obtenerEstadisticasHerramientas());
    }
}