package com.rentaherramientas.plataforma.seguridad;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. RECURSOS PÚBLICOS Y SWAGGER
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/herramientas/**").permitAll()

                        // 2. RESERVAS - AJUSTE DE PRIORIDAD
                        // Ponemos la ruta del proveedor primero para asegurar que no se confunda con /api/reservas/**
                        .requestMatchers("/api/reservas/proveedor/**").hasAnyRole("PROVEEDOR", "ADMINISTRADOR")
                        .requestMatchers("/api/reservas/mis-reservas/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/reservas/cliente/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")

                        // Operaciones específicas
                        .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/devolucion").hasAnyRole("PROVEEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")

                        // 3. HERRAMIENTAS - ESCRITURA
                        .requestMatchers(HttpMethod.POST, "/api/herramientas/**").hasAnyRole("PROVEEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/herramientas/**").hasAnyRole("PROVEEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/herramientas/**").hasAnyRole("PROVEEDOR", "ADMINISTRADOR")

                        // 4. ADMIN Y USUARIOS
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")

                        // 5. REGLA GENERAL PARA RESERVAS Y EL RESTO
                        .requestMatchers("/api/reservas/**").authenticated()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Agregamos '*' temporalmente si sigues teniendo problemas de CORS, o mantén tus puertos
        config.setAllowedOrigins(Arrays.asList("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000", "http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}