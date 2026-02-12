package com.rentaherramientas.plataforma.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        // 1. Validación de Header
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            try {
                String username = jwtUtil.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(jwt, username)) {

                        // 2. Extracción segura de Roles
                        List<?> rolesRaw = jwtUtil.extractRoles(jwt);
                        if (rolesRaw == null) rolesRaw = new ArrayList<>();

                        // 3. Mapeo a Authorities con normalización estricta
                        List<SimpleGrantedAuthority> authorities = rolesRaw.stream()
                                .map(Object::toString) // Convertir a String de forma segura
                                .map(String::trim)
                                .map(String::toUpperCase)
                                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        // DEBUG: Revisa esto en la consola de tu IDE cuando des clic al botón
                        System.out.println(">>> JWT FILTER - Usuario: " + username);
                        System.out.println(">>> JWT FILTER - Authorities: " + authorities);

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                // Enviar un log claro para debuguear en el examen
                System.err.println("JWT FILTER ERROR: " + e.getMessage());
                // No lanzamos excepción aquí para que la cadena siga y devuelva el 403 correctamente
            }
        }

        chain.doFilter(request, response);
    }
}