package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.servicio.ReservaService;
import com.rentaherramientas.plataforma.repositorio.HerramientaRepositorio;
import com.rentaherramientas.plataforma.repositorio.UsuarioRepositorio;
import com.rentaherramientas.plataforma.repositorio.ReservaRepositorio; // 1. Importa el repo
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
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'ROLE_ADMINISTRADOR')")
public class AdminControlador {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private HerramientaRepositorio herramientaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ReservaRepositorio reservaRepositorio; // 2. Inyecta el repo

    @GetMapping("/dashboard")
    @Operation(summary = "Resumen global para el Administrador")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // Obtenemos el valor y validamos que no sea nulo
            Double ingresos = reservaService.obtenerTotalIngresos();
            respuesta.put("gananciasTotales", (ingresos != null) ? ingresos : 0.0);
        } catch (Exception e) {
            // Si el servicio falla, ponemos 0 para que el frontend no de error
            System.err.println("Error al calcular ingresos: " + e.getMessage());
            respuesta.put("gananciasTotales", 0.0);
        }

        // Datos vistos por al admin en el dashboard
        respuesta.put("totalHerramientas", herramientaRepositorio.count());
        respuesta.put("totalReservas", reservaRepositorio.count());
        respuesta.put("totalUsuarios", usuarioRepositorio.count());

        return ResponseEntity.ok(respuesta);
    }
}