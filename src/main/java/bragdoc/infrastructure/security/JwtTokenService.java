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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Implementação do serviço de tokens JWT.
 *
 * Responsabilidades:
 * - Gerar tokens JWT
 * - Validar tokens JWT
 * - Extrair informações dos tokens
 */
@Service
public class JwtTokenService implements AuthTokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:900}") long accessTokenExpirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    @Override
    public String generateToken(String userLogin, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpirationSeconds);

        Map<String, Object> allClaims = new HashMap<>();
        allClaims.put("login", userLogin);
        if (claims != null) {
            allClaims.putAll(claims);
        }

        return Jwts.builder()
                .claims(allClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String extractUserLogin(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("login", String.class);
        } catch (ExpiredJwtException e) {
            throw new bragdoc.domain.shared.exceptions.TokenExpiredException();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrai claims de um token.
     * Lança ExpiredJwtException se o token estiver expirado.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica se um token está expirado.
     * Útil para lógica de renovação.
     */
    public boolean isExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            // Token inválido também é considerado "expirado"
            return true;
        }
    }

    /**
     * Extrai o login do usuário mesmo se o token estiver expirado.
     * Útil para renovação automática de tokens.
     */
    public String extractUserLoginSafe(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("login", String.class);
        } catch (ExpiredJwtException e) {
            // Token expirado, mas ainda podemos extrair as claims
            return e.getClaims().get("login", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
