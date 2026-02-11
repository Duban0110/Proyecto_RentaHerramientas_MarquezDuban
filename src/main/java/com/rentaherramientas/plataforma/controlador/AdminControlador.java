package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.servicio.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
@PreAuthorize("hasAuthority('ADMINISTRADOR')") // O hasRole según tu config
public class AdminControlador {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/ingresos")
    public ResponseEntity<?> verIngresos() {
        return ResponseEntity.ok(Map.of("totalIngresos", reservaService.obtenerTotalIngresos()));
    }

    @GetMapping("/popularidad")
    public ResponseEntity<?> verPopularidad() {
        return ResponseEntity.ok(reservaService.obtenerEstadisticasHerramientas());
    }
}