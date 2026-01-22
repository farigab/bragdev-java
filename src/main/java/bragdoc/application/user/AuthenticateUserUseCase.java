package bragdoc.application.user;

import java.util.Map;

import bragdoc.application.user.dto.AuthResponse;
import bragdoc.application.user.dto.UserResponse;
import bragdoc.domain.user.AuthTokenService;
import bragdoc.domain.user.OAuthService;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Autenticar usuário via GitHub OAuth.
 */
public class AuthenticateUserUseCase {

    private final OAuthService oauthService;
    private final UserRepository userRepository;
    private final AuthTokenService tokenService;

    public AuthenticateUserUseCase(
            OAuthService oauthService,
            UserRepository userRepository,
            AuthTokenService tokenService) {
        this.oauthService = oauthService;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthResponse execute(String code, String redirectUri) {
        // 1. Trocar código por access token
        String githubAccessToken = oauthService.exchangeCodeForToken(code, redirectUri);

        // 2. Buscar perfil do GitHub
        var profile = oauthService.getUserProfile(githubAccessToken);

        // 3. Criar ou atualizar usuário
        User user = userRepository.findByLogin(profile.login())
                .orElseGet(() -> User.create(profile.login(), profile.name(), profile.avatarUrl()));

        user.updateProfile(profile.name(), profile.avatarUrl());
        user.setGitHubToken(githubAccessToken);

        User savedUser = userRepository.save(user);

        // 4. Gerar JWT
        String jwt = tokenService.generateToken(
                savedUser.getLogin(),
                Map.of(
                        "name", savedUser.getName(),
                        "avatar", savedUser.getAvatarUrl() != null ? savedUser.getAvatarUrl() : ""));

        return new AuthResponse(jwt, UserResponse.from(savedUser));
    }
}
