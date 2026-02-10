package com.rentaherramientas.plataforma.controlador;

import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // MÉTODO PARA REGISTRAR (POST)
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrar(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // MÉTODO PARA LISTAR (GET) - Opcional para pruebas
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        // Aquí podrías llamar a un método findAll() en el service si lo tienes
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
}