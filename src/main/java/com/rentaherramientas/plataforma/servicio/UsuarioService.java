package com.rentaherramientas.plataforma.servicio;

import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepositorio.save(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    // --- NUEVO MÉTODO PARA EL ADMINISTRADOR ---
    public void eliminar(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepositorio.deleteById(id);
    }
}