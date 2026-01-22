package bragdoc.domain.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import bragdoc.domain.shared.exceptions.DomainException;

/**
 * Entidade de domínio RefreshToken - representa um token de renovação.
 * Livre de frameworks e anotações.
 */
public class RefreshToken {
    private String token;
    private String userLogin;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean revoked;

    private RefreshToken(String token, String userLogin, Instant expiresAt,
            Instant createdAt, boolean revoked) {
        this.token = validateToken(token);
        this.userLogin = validateUserLogin(userLogin);
        this.expiresAt = validateExpiresAt(expiresAt);
        this.createdAt = createdAt;
        this.revoked = revoked;
    }

    /**
     * Cria um novo refresh token com validade de 7 dias.
     */
    public static RefreshToken create(String userLogin) {
        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(7 * 24 * 60 * 60); // 7 dias

        return new RefreshToken(token, userLogin, expiresAt, now, false);
    }

    /**
     * Reconstrói um refresh token existente.
     */
    public static RefreshToken restore(String token, String userLogin,
            Instant expiresAt, Instant createdAt,
            boolean revoked) {
        return new RefreshToken(token, userLogin, expiresAt, createdAt, revoked);
    }

    /**
     * Verifica se o token está válido (não expirado e não revogado).
     */
    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    /**
     * Verifica se o token está expirado.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Revoga o token.
     */
    public void revoke() {
        if (revoked) {
            throw new DomainException("Token já está revogado");
        }
        this.revoked = true;
    }

    private String validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new DomainException("Token é obrigatório");
        }
        return token;
    }

    private String validateUserLogin(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            throw new DomainException("Login do usuário é obrigatório");
        }
        return userLogin;
    }

    private Instant validateExpiresAt(Instant expiresAt) {
        if (expiresAt == null) {
            throw new DomainException("Data de expiração é obrigatória");
        }
        if (expiresAt.isBefore(Instant.now())) {
            throw new DomainException("Data de expiração não pode ser no passado");
        }
        return expiresAt;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RefreshToken that))
            return false;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
