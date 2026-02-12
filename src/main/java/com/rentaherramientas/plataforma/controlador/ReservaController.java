package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.*;
import com.rentaherramientas.plataforma.servicio.ReservaService;
import com.rentaherramientas.plataforma.seguridad.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<?> crear(@RequestBody ReservaRequestDTO dto) {
        try {
            return ResponseEntity.ok(reservaService.crearReserva(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Listar reservas del cliente autenticado")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas(
            @RequestHeader(value = "Authorization", required = false) String token,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(reservaService.listarPorCorreo(obtenerCorreo(token, userDetails)));
    }

    @GetMapping("/proveedor")
    @Operation(summary = "Listar herramientas alquiladas al proveedor")
    @PreAuthorize("hasAnyAuthority('PROVEEDOR', 'ROLE_PROVEEDOR', 'ADMINISTRADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasParaProveedor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(reservaService.listarParaProveedor(obtenerCorreo(token, userDetails)));
    }

    @PatchMapping("/{id}/devolucion")
    @Operation(summary = "Confirmar devolución de herramienta")
    @PreAuthorize("hasAnyAuthority('PROVEEDOR', 'ROLE_PROVEEDOR', 'ADMINISTRADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> confirmarDevolucion(@PathVariable Long id) {
        try {
            DevolucionRequestDTO dto = new DevolucionRequestDTO();
            dto.setReservaId(id);
            return ResponseEntity.ok(reservaService.completarDevolucion(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pagar/descargar")
    @Operation(summary = "Generar y descargar factura en PDF")
    public ResponseEntity<byte[]> descargarFactura(@RequestBody PagoRequestDTO dto) {
        try {
            byte[] pdfBytes = reservaService.generarFacturaPDF(dto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_" + dto.getReservaId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String obtenerCorreo(String token, UserDetails userDetails) {
        if (userDetails != null) return userDetails.getUsername();
        if (token != null && token.startsWith("Bearer ")) {
            return jwtUtil.extractUsername(token.substring(7));
        }
        throw new RuntimeException("Usuario no identificado");
    }
}