package bragdoc.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bragdoc.domain.shared.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:900}") long accessTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String generateAccessToken(String userLogin, Map<String, Object> claims) {
        return generateToken(userLogin, claims, accessTokenExpiration);
    }

    public String generateToken(String userLogin, Map<String, Object> claims) {
        return generateToken(userLogin, claims, accessTokenExpiration);
    }

    private String generateToken(String userLogin, Map<String, Object> claims, long expirationSeconds) {
        var now = Instant.now();
        var allClaims = new java.util.HashMap<String, Object>();
        allClaims.put("login", userLogin);

        if (claims != null) {
            allClaims.putAll(claims);
        }

        return Jwts.builder()
                .claims(allClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
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
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        }
    }

    /**
     * Extrai o login mesmo se o token estiver expirado
     */
    public String extractUserLoginSafe(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)  
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("login", String.class);
        } catch (ExpiredJwtException e) {
            // Token expirado, mas ainda podemos extrair as claims
            return e.getClaims().get("login", String.class);
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

    public boolean isExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
