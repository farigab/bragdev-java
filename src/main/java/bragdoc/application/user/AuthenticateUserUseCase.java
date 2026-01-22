package bragdoc.application.user;

import java.util.Map;

import bragdoc.application.user.dto.AuthResponse;
import bragdoc.application.user.dto.UserResponse;
import bragdoc.domain.user.AuthTokenService;
import bragdoc.domain.user.OAuthService;
import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Autenticar usuário via GitHub OAuth.
 */
public class AuthenticateUserUseCase {

    private final OAuthService oauthService;
    private final UserRepository userRepository;
    private final AuthTokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticateUserUseCase(
            OAuthService oauthService,
            UserRepository userRepository,
            AuthTokenService tokenService,
            RefreshTokenRepository refreshTokenRepository) {
        this.oauthService = oauthService;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponse execute(String code, String redirectUri) {
        // 1. Trocar código por access token do GitHub
        String githubAccessToken = oauthService.exchangeCodeForToken(code, redirectUri);

        // 2. Buscar perfil do GitHub
        var profile = oauthService.getUserProfile(githubAccessToken);

        // 3. Criar ou atualizar usuário
        User user = userRepository.findByLogin(profile.login())
                .orElseGet(() -> User.create(profile.login(), profile.name(), profile.avatarUrl()));

        user.updateProfile(profile.name(), profile.avatarUrl());
        User savedUser = userRepository.save(user);

        // 4. Gerar JWT (access token)
        String jwt = tokenService.generateToken(
                savedUser.getLogin(),
                Map.of(
                        "name", savedUser.getName(),
                        "avatar", savedUser.getAvatarUrl() != null ? savedUser.getAvatarUrl() : ""));

        // 5. Criar refresh token
        RefreshToken refreshToken = RefreshToken.create(savedUser.getLogin());
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(jwt, refreshToken.getToken(), UserResponse.from(savedUser));
    }
}
