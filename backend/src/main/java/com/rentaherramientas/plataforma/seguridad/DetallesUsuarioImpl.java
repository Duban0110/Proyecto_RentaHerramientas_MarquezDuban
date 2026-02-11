package com.rentaherramientas.plataforma.seguridad;

import com.rentaherramientas.plataforma.entidad.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class DetallesUsuarioImpl implements UserDetails {

    private final Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Al ser Enum, usamos .name() para obtener "ADMINISTRADOR", etc.
        return Collections.singleton(new SimpleGrantedAuthority(usuario.getRol().name()));
    }

    @Override
    public String getPassword() { return usuario.getContrasena(); }
    @Override
    public String getUsername() { return usuario.getCorreo(); }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}