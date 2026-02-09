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
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        return new DetallesUsuarioImpl(usuario);
    }
}