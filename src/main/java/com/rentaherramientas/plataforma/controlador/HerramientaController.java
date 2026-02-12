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

    // --- LISTAR HERRAMIENTAS ---
    // Ahora devuelve el catálogo completo visible para todos
    @GetMapping
    public List<HerramientaDTO> listarHerramientas() {
        return herramientaRepositorio.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // --- GUARDAR NUEVA HERRAMIENTA ---
    @PostMapping
    public ResponseEntity<?> crearHerramienta(@RequestBody Herramienta herramienta) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para publicar");
            }

            String correoElectronico = auth.getName();
            Usuario proveedor = usuarioRepositorio.findByCorreo(correoElectronico)
                    .orElseThrow(() -> new RuntimeException("Perfil de proveedor no encontrado"));

            herramienta.setProveedor(proveedor);

            // --- VALIDACIÓN Y LÓGICA DE NEGOCIO ---
            if (herramienta.getNombre() == null || herramienta.getPrecioDia() == null) {
                return ResponseEntity.badRequest().body("Nombre y Precio son campos obligatorios");
            }

            if (herramienta.getStock() == null) {
                herramienta.setStock(1);
            }

            // IMPORTANTE: Sincronizar el campo 'disponible' de la Entidad con el stock
            // Esto evita que la DB rechace el INSERT por falta del campo
            herramienta.setDisponible(herramienta.getStock() > 0);

            // 4. Persistencia
            Herramienta nuevaHerramienta = herramientaRepositorio.save(herramienta);

            System.out.println("✅ Herramienta '" + nuevaHerramienta.getNombre() + "' creada por: " + correoElectronico);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(nuevaHerramienta));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    // --- MAPEO DE ENTIDAD A DTO ---
    private HerramientaDTO convertirADTO(Herramienta herramienta) {
        if (herramienta == null) return null;

        HerramientaDTO dto = new HerramientaDTO();
        dto.setId(herramienta.getId());
        dto.setNombre(herramienta.getNombre());
        dto.setDescripcion(herramienta.getDescripcion());
        dto.setPrecioDia(herramienta.getPrecioDia());
        dto.setStock(herramienta.getStock());

        // Lógica automática de disponibilidad basada en stock
        dto.setDisponible(herramienta.getStock() != null && herramienta.getStock() > 0);

        // Construcción segura del nombre del proveedor (Nombre + Apellido)
        if (herramienta.getProveedor() != null) {
            String nombre = (herramienta.getProveedor().getNombre() != null) ? herramienta.getProveedor().getNombre() : "";
            String apellido = (herramienta.getProveedor().getApellido() != null) ? herramienta.getProveedor().getApellido() : "";
            String nombreCompleto = (nombre + " " + apellido).trim();

            dto.setNombreProveedor(nombreCompleto.isEmpty() ? "Proveedor Desconocido" : nombreCompleto);
        } else {
            dto.setNombreProveedor("Sin Proveedor");
        }

        return dto;
    }
}