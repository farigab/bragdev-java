package bragdoc.application.user.dto;

/**
 * DTO de requisição para renovação de token.
 */
public record RefreshTokenRequest(String refreshToken) {
    public RefreshTokenRequest {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token é obrigatório");
        }
    }
}
