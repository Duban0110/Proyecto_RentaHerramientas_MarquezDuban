package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.ReservaRequestDTO;
import com.rentaherramientas.plataforma.dto.ReservaResponseDTO;
import com.rentaherramientas.plataforma.servicio.ReservaService;
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

    // Este endpoint es automático: sabe quién eres por tu Login
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(reservaService.listarPorEmail(email));
    }

    // Este lo puede usar el Admin para buscar a cualquier cliente
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