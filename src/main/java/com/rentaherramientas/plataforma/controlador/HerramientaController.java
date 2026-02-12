package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.HerramientaDTO;
import com.rentaherramientas.plataforma.entidad.Herramienta;
import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.repositorio.HerramientaRepositorio;
import com.rentaherramientas.plataforma.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {

    @Autowired
    private HerramientaRepositorio herramientaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping
    public List<HerramientaDTO> listarHerramientas() {
        return herramientaRepositorio.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> crearHerramienta(@RequestBody Herramienta herramienta) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión inválida");
            }

            Usuario proveedor = usuarioRepositorio.findByCorreo(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            if (herramienta.getNombre() == null || herramienta.getPrecioDia() == null) {
                return ResponseEntity.badRequest().body("Nombre y Precio son requeridos");
            }

            herramienta.setProveedor(proveedor);
            if (herramienta.getStock() == null) herramienta.setStock(1);
            herramienta.setDisponible(herramienta.getStock() > 0);

            Herramienta nuevaHerramienta = herramientaRepositorio.save(herramienta);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(nuevaHerramienta));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private HerramientaDTO convertirADTO(Herramienta herramienta) {
        if (herramienta == null) return null;

        HerramientaDTO dto = new HerramientaDTO();
        dto.setId(herramienta.getId());
        dto.setNombre(herramienta.getNombre());
        dto.setDescripcion(herramienta.getDescripcion());
        dto.setPrecioDia(herramienta.getPrecioDia());
        dto.setStock(herramienta.getStock());
        dto.setImagenUrl(herramienta.getImagenUrl());
        dto.setDisponible(herramienta.getStock() != null && herramienta.getStock() > 0);

        if (herramienta.getProveedor() != null) {
            String nombreCompleto = (herramienta.getProveedor().getNombre() + " " +
                    herramienta.getProveedor().getApellido()).trim();
            dto.setNombreProveedor(nombreCompleto.isEmpty() ? "Usuario sin nombre" : nombreCompleto);
        } else {
            dto.setNombreProveedor("Sin Proveedor");
        }

        return dto;
    }
}