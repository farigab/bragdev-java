package bragdoc.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bragdoc.domain.user.AuthTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Implementação do serviço de tokens JWT.
 */
@Service
public class JwtTokenService implements AuthTokenService {

    private final SecretKey secretKey;

    public JwtTokenService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String userLogin, Map<String, Object> claims) {
        var now = Instant.now();

        var allClaims = new HashMap<String, Object>();
        allClaims.put("login", userLogin);
        if (claims != null) {
            allClaims.putAll(claims);
        }

        return Jwts.builder()
                .claims(allClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
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

    @Override
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
