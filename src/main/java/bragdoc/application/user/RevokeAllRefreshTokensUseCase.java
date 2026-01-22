package bragdoc.application.user;

import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;

import java.util.List;

/**
 * Caso de uso: Revogar todos os refresh tokens de um usuário.
 * Útil para logout em todos os dispositivos.
 */
public class RevokeAllRefreshTokensUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeAllRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String userLogin) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserLogin(userLogin);

        tokens.forEach(token -> {
            if (!token.isRevoked()) {
                token.revoke();
                refreshTokenRepository.save(token);
            }
        });
    }
}
