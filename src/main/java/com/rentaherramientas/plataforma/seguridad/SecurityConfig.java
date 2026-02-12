package com.rentaherramientas.plataforma.seguridad;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. RECURSOS DEL SISTEMA Y AUTH
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll() // IMPORTANTE: Permite que Spring maneje sus propios errores
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. USUARIOS
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Registro público

                        // 3. HERRAMIENTAS
                        .requestMatchers(HttpMethod.GET, "/api/herramientas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/herramientas/**").hasAnyAuthority("PROVEEDOR", "ROLE_PROVEEDOR", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/herramientas").hasAnyAuthority("PROVEEDOR", "ROLE_PROVEEDOR", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")

                        // 4. REPORTES Y ADMIN (Mover arriba para asegurar prioridad)
                        .requestMatchers("/api/admin/reportes/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")

                        // 5. RESERVAS
                        .requestMatchers("/api/reservas/proveedor").hasAnyAuthority("PROVEEDOR", "ROLE_PROVEEDOR", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/devolucion").hasAnyAuthority("PROVEEDOR", "ROLE_PROVEEDOR", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/reservas/mis-reservas").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservas").authenticated()
                        .requestMatchers("/api/reservas/**").authenticated()

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception { return authConfig.getAuthenticationManager(); }
    @Bean public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}