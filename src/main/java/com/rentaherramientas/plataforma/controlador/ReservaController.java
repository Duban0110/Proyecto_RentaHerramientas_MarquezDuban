package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.*;
import com.rentaherramientas.plataforma.servicio.ReservaService;
import com.rentaherramientas.plataforma.seguridad.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

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
            @RequestHeader("Authorization") String token,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        String correo = (userDetails != null) ? userDetails.getUsername() : jwtUtil.extractUsername(token.substring(7));
        return ResponseEntity.ok(reservaService.listarPorCorreo(correo));
    }

    @PutMapping("/devolver")
    public ResponseEntity<?> devolver(@RequestBody DevolucionRequestDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.completarDevolucion(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pagar/descargar")
    public ResponseEntity<byte[]> descargarFactura(@RequestBody PagoRequestDTO dto) {
        // Ahora el símbolo procesarPagoSimulado ya existe en el Service
        String facturaTexto = reservaService.procesarPagoSimulado(dto);
        byte[] facturaBytes = facturaTexto.getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=factura_reserva_" + dto.getReservaId() + ".txt")
                .body(facturaBytes);
    }
}