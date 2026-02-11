package com.rentaherramientas.plataforma.seguridad;

import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetallesUsuarioServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String correoInput) throws UsernameNotFoundException {
        // 1. Buscamos al usuario por correo
        Usuario usuario = usuarioRepositorio.findByCorreo(correoInput)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con correo: " + correoInput));

        // 2. RETORNO CLAVE: Usamos tu clase personalizada DetallesUsuarioImpl
        // Esto soluciona el Error 500 porque unifica el tipo de objeto en Swagger
        return new DetallesUsuarioImpl(usuario);
    }
}