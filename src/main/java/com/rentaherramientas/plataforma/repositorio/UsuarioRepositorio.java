package com.rentaherramientas.plataforma.repositorio;

import com.rentaherramientas.plataforma.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // metodo del login
    Optional<Usuario> findByCorreo(String correo);
}