package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.dto.UsuarioDTO;
import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrar(usuario);
            return ResponseEntity.ok(mapearDTO(nuevo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos().stream()
                .map(this::mapearDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    private UsuarioDTO mapearDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setRol(u.getRol() != null ? u.getRol().name() : null);
        return dto;
    }
}