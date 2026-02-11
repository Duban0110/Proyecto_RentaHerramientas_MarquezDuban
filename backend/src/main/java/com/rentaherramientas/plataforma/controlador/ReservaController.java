package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.DevolucionRequestDTO;
import com.rentaherramientas.plataforma.dto.PagoRequestDTO;
import com.rentaherramientas.plataforma.dto.ReservaRequestDTO;
import com.rentaherramientas.plataforma.dto.ReservaResponseDTO;
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

    // 1. Crear una nueva reserva (Resta stock automáticamente)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequestDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.crearReserva(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Listar reservas del usuario actual (Para el Dashboard del Cliente)
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas(
            @RequestHeader("Authorization") String token,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        String correo;

        // Verificación doble para evitar el error de "userDetails is null"
        if (userDetails != null) {
            correo = userDetails.getUsername();
        } else {
            // Extraemos manualmente si el contexto de Spring falla
            String jwt = token.substring(7);
            correo = jwtUtil.extractUsername(jwt);
        }

        return ResponseEntity.ok(reservaService.listarPorCorreo(correo));
    }

    // 3. Listar por ID de Cliente (Útil para que el Admin vea historial de alguien)
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorCliente(id));
    }

    // 4. Devolución de herramienta (Suma stock automáticamente)
    @PutMapping("/devolver")
    public ResponseEntity<?> devolver(@RequestBody DevolucionRequestDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.completarDevolucion(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Simular pago y generar texto de factura
    @PostMapping("/pagar/descargar")
    public ResponseEntity<byte[]> descargarFactura(@RequestBody PagoRequestDTO dto) {
        String facturaTexto = reservaService.procesarPagoSimulado(dto);
        byte[] facturaBytes = facturaTexto.getBytes();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=factura_reserva_" + dto.getReservaId() + ".txt")
                .body(facturaBytes);
    }
}