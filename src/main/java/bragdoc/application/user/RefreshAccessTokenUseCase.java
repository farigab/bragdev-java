package bragdoc.application.user;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import bragdoc.application.user.dto.AuthResponse;
import bragdoc.application.user.dto.UserResponse;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.domain.user.AuthTokenService;
import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Renovar access token usando refresh token.
 *
 * Responsabilidades:
 * - Validar refresh token
 * - Gerar novo access token
 * - Realizar rotação de refresh token (rotation)
 * - Manter controle transacional
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

    @Transactional
    public AuthResponse execute(String refreshTokenValue) {
        // 1. Buscar e validar refresh token
        RefreshToken refreshToken = findAndValidateRefreshToken(refreshTokenValue);

        // 2. Buscar usuário
        User user = findUser(refreshToken.getUserLogin());

        // 3. Gerar novo access token
        String newAccessToken = generateAccessToken(user);

        // 4. Realizar rotação do refresh token (segurança)
        RefreshToken newRefreshToken = rotateRefreshToken(refreshToken);

        // 5. Retornar resposta
        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                UserResponse.from(user));
    }

    private RefreshToken findAndValidateRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido ou não encontrado"));

        if (!token.isValid()) {
            handleInvalidToken(token);
        }

        return token;
    }

    private void handleInvalidToken(RefreshToken token) {
        if (token.isRevoked()) {
            // Token foi revogado manualmente (logout, por exemplo)
            throw new UnauthorizedException("Refresh token foi revogado");
        }

        if (token.isExpired()) {
            // Token expirou - limpar do banco
            refreshTokenRepository.delete(token);
            throw new UnauthorizedException("Refresh token expirado. Faça login novamente");
        }

        throw new UnauthorizedException("Refresh token inválido");
    }

    private User findUser(String userLogin) {
        return userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    private String generateAccessToken(User user) {
        Map<String, Object> claims = Map.of(
                "name", user.getName(),
                "avatar", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");

        return authTokenService.generateToken(user.getLogin(), claims);
    }

    /**
     * Rotação de refresh token - boa prática de segurança.
     * O token antigo é revogado e um novo é criado.
     */
    private RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // Revogar token antigo
        oldToken.revoke();
        refreshTokenRepository.save(oldToken);

        // Criar e salvar novo token
        RefreshToken newToken = RefreshToken.create(oldToken.getUserLogin());
        return refreshTokenRepository.save(newToken);
    }
}
