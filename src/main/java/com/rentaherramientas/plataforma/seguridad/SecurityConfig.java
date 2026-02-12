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
                        // 1. RECURSOS PÚBLICOS
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/herramientas/**").permitAll()

                        // 2. ADMINISTRACIÓN (Uso de hasAnyAuthority para evitar problemas de prefijo ROLE_)
                        // Esto permite que funcione tanto si el token trae "ADMINISTRADOR" como "ROLE_ADMINISTRADOR"
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // 3. RESERVAS
                        .requestMatchers("/api/reservas/proveedor/**").hasAnyAuthority("ROLE_PROVEEDOR", "PROVEEDOR", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers("/api/reservas/mis-reservas/**").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers("/api/reservas/cliente/**").hasAnyAuthority("ROLE_CLIENTE", "CLIENTE", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // 4. HERRAMIENTAS - ESCRITURA
                        .requestMatchers(HttpMethod.POST, "/api/herramientas/**").hasAnyAuthority("ROLE_PROVEEDOR", "PROVEEDOR", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/herramientas/**").hasAnyAuthority("ROLE_PROVEEDOR", "PROVEEDOR", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/herramientas/**").hasAnyAuthority("ROLE_PROVEEDOR", "PROVEEDOR", "ROLE_ADMINISTRADOR", "ADMINISTRADOR")

                        // 5. REGLA GENERAL
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Agregamos orígenes comunes de Live Server y puertos de frameworks
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
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