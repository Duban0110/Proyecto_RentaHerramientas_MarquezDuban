package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.servicio.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
public class AdminControlador {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/ingresos")
    public ResponseEntity<Double> verIngresos() {
        return ResponseEntity.ok(reservaService.obtenerTotalIngresos());
    }

    @GetMapping("/herramientas-populares")
    public ResponseEntity<List<Map<String, Object>>> verEstadisticas() {
        // AQUÍ ESTABA EL ERROR: El nombre debe coincidir con el Service
        return ResponseEntity.ok(reservaService.obtenerEstadisticasHerramientas());
    }
}