package com.rentaherramientas.plataforma.seguridad;

import com.rentaherramientas.plataforma.entidad.Usuario;
import com.rentaherramientas.plataforma.repositorio.UsuarioRepositorio; // <--- AGREGAMOS ESTO
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired; // <--- AGREGAMOS ESTO
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // <--- Inyectamos el repo para buscar el ID seguro

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // MÉTODO INFALIBLE: Buscamos el usuario en la DB por su correo para asegurar el ID
        Usuario usuario = usuarioRepositorio.findByCorreo(userDetails.getUsername())
                .orElse(null);

        if (usuario != null) {
            claims.put("id", usuario.getId());
            System.out.println("ID inyectado en Token: " + usuario.getId()); // Ver esto en consola de IntelliJ
        }

        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // ... (el resto de tus métodos extractUsername, validateToken, etc., están perfectos, no los cambies)

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}