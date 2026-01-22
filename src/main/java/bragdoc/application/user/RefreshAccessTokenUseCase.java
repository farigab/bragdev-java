package bragdoc.application.user;

import java.util.Map;

import bragdoc.application.user.dto.AuthResponse;
import bragdoc.application.user.dto.UserResponse;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.TokenExpiredException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.domain.user.AuthTokenService;
import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Renovar access token usando refresh token.
 */
public class RefreshAccessTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    public RefreshAccessTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            AuthTokenService authTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.authTokenService = authTokenService;
    }

    public AuthResponse execute(String refreshTokenValue) {
        // 1. Buscar refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        // 2. Validar token
        if (!refreshToken.isValid()) {
            if (refreshToken.isRevoked()) {
                throw new UnauthorizedException("Refresh token foi revogado");
            }
            if (refreshToken.isExpired()) {
                // Limpar token expirado
                refreshTokenRepository.delete(refreshToken);
                throw new TokenExpiredException();
            }
            throw new UnauthorizedException("Refresh token inválido");
        }

        // 3. Buscar usuário
        User user = userRepository.findByLogin(refreshToken.getUserLogin())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // 4. Gerar novo access token
        String newAccessToken = authTokenService.generateToken(
                user.getLogin(),
                Map.of(
                        "name", user.getName(),
                        "avatar", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""));

        // 5. Criar novo refresh token e revogar o antigo
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = RefreshToken.create(user.getLogin());
        refreshTokenRepository.save(newRefreshToken);

        // 6. Retornar resposta com novos tokens
        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                UserResponse.from(user));
    }
}
