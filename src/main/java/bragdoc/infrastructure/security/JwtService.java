package bragdoc.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Serviço de infraestrutura para operações com JWT.
 * Isolado das camadas superiores.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userLogin, Map<String, Object> claims) {
        var now = Instant.now();
        var allClaims = Map.<String, Object>of("login", userLogin);

        if (claims != null) {
            var merged = new java.util.HashMap<>(allClaims);
            merged.putAll(claims);
            allClaims = merged;
        }
        return Jwts.builder()
                .claims(allClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

    }

    public String extractUserLogin(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("login", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
