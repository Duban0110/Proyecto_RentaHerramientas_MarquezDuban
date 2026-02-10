package com.rentaherramientas.plataforma.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Solo los ADMIN pueden ver todas las reservas de todo el mundo
                        .requestMatchers(HttpMethod.GET, "/api/reservas").hasRole("ADMIN")

                        // Solo los CLIENTES pueden crear reservas
                        .requestMatchers(HttpMethod.POST, "/api/reservas/**").hasRole("CLIENTE")

                        // Los PROVEEDORES podrían tener acceso a gestionar sus herramientas
                        .requestMatchers("/api/herramientas/**").hasAnyRole("PROVEEDOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // O JWT si lo estás usando

        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}