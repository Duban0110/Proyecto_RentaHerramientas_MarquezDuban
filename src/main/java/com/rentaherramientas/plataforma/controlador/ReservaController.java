package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.ReservaRequestDTO;
import com.rentaherramientas.plataforma.dto.ReservaResponseDTO;
import com.rentaherramientas.plataforma.servicio.ReservaService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequestDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.crearReserva(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas(
            @Parameter(hidden = true) // ESTO OCULTA EL OBJETO COMPLEJO Y QUITA EL ERROR 500
            @AuthenticationPrincipal UserDetails userDetails) {

        // Obtenemos el correo desde la sesión
        String correo = userDetails.getUsername();
        return ResponseEntity.ok(reservaService.listarPorCorreo(correo));
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorCliente(id));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.completarDevolucion(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}