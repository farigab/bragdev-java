package bragdoc.application.user;

import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;

/**
 * Caso de uso: Revogar um refresh token específico.
 */
public class RevokeRefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String refreshTokenValue, String userLogin) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token não encontrado"));

        // Verificar se o token pertence ao usuário
        if (!refreshToken.getUserLogin().equals(userLogin)) {
            throw new UnauthorizedException("Acesso negado");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }
}
