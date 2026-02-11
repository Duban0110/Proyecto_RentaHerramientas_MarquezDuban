package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.UsuarioDTO;
import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios y registro")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en la base de datos con la contraseña cifrada.")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody Usuario usuario) {
        // El servicio se encarga de cifrar la contraseña
        Usuario nuevo = usuarioService.registrar(usuario);
        return ResponseEntity.ok(mapearDTO(nuevo));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista de usuarios registrados (solo datos públicos).")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos()
                .stream()
                .map(this::mapearDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Convierte la Entidad Usuario a UsuarioDTO para evitar exponer
     * datos sensibles como la contraseña en Swagger o en la respuesta API.
     */
    private UsuarioDTO mapearDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setCorreo(u.getCorreo());
        // Validamos que el rol no sea nulo antes de llamar a .name()
        dto.setRol(u.getRol() != null ? u.getRol().name() : null);
        return dto;
    }
}