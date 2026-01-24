package bragdoc.interfaces.api.auth;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import bragdoc.application.user.AuthenticateUserUseCase;
import bragdoc.application.user.ClearGitHubTokenUseCase;
import bragdoc.application.user.RefreshAccessTokenUseCase;
import bragdoc.application.user.RevokeAllRefreshTokensUseCase;
import bragdoc.application.user.SaveGitHubTokenUseCase;
import bragdoc.application.user.dto.AuthResponse;
import bragdoc.application.user.dto.SaveGitHubTokenRequest;
import bragdoc.interfaces.api.common.CurrentUser;
import bragdoc.interfaces.api.user.dto.SaveGitHubTokenApiRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação e OAuth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final RevokeAllRefreshTokensUseCase revokeAllRefreshTokensUseCase;
    private final SaveGitHubTokenUseCase saveGitHubTokenUseCase;
    private final ClearGitHubTokenUseCase clearGitHubTokenUseCase;

    @Value("${github.oauth.client-id}")
    private String clientId;

    @Value("${github.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${github.oauth.frontend-redirect-uri}")
    private String frontendRedirectUri;

    @Value("${app.cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase,
            RevokeAllRefreshTokensUseCase revokeAllRefreshTokensUseCase,
            SaveGitHubTokenUseCase saveGitHubTokenUseCase,
            ClearGitHubTokenUseCase clearGitHubTokenUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
        this.revokeAllRefreshTokensUseCase = revokeAllRefreshTokensUseCase;
        this.saveGitHubTokenUseCase = saveGitHubTokenUseCase;
        this.clearGitHubTokenUseCase = clearGitHubTokenUseCase;
    }

    /**
     * Inicia o fluxo OAuth do GitHub.
     */
    @GetMapping("/github")
    public ResponseEntity<Void> redirectToGitHub() {
        String state = UUID.randomUUID().toString();

        String authorizeUrl = UriComponentsBuilder
                .fromUriString("https://github.com/login/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "read:user,user:email")
                .queryParam("state", state)
                .build(true)
                .toUriString();

        return ResponseEntity.status(302)
                .location(URI.create(authorizeUrl))
                .build();
    }

    /**
     * Callback do GitHub OAuth.
     * Troca o código por tokens e cria cookies de autenticação.
     */
    @GetMapping("/callback")
    public void callback(
            @RequestParam String code,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authenticateUserUseCase.execute(code, redirectUri);

            setAuthenticationCookies(response, authResponse);

            log.info("Usuário autenticado: {}", authResponse.user().login());
            response.sendRedirect(frontendRedirectUri);

        } catch (Exception e) {
            log.error("Erro ao autenticar usuário: {}", e.getMessage());
            try {
                response.sendRedirect(frontendRedirectUri + "/login?error=auth_failed");
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao redirecionar", ex);
            }
        }
    }

    /**
     * Renova o access token usando o refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            clearAuthenticationCookies(response);
            return ResponseEntity.status(401).build();
        }

        try {
            AuthResponse authResponse = refreshAccessTokenUseCase.execute(refreshToken);
            setAuthenticationCookies(response, authResponse);

            log.info("Token renovado com sucesso");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.warn("Falha ao renovar token: {}", e.getMessage());
            clearAuthenticationCookies(response);
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Logout - revoga todos os refresh tokens e limpa cookies.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CurrentUser String userLogin,
            HttpServletResponse response) {

        revokeAllRefreshTokensUseCase.execute(userLogin);
        clearAuthenticationCookies(response);

        log.info("Logout realizado: {}", userLogin);
        return ResponseEntity.ok().build();
    }

    /**
     * Salva token do GitHub para importações.
     */
    @PostMapping("/github/token")
    public ResponseEntity<Void> saveGitHubToken(
            @Valid @RequestBody SaveGitHubTokenApiRequest apiRequest,
            @CurrentUser String userLogin) {

        var request = new SaveGitHubTokenRequest(apiRequest.token());
        saveGitHubTokenUseCase.execute(request, userLogin);

        return ResponseEntity.ok().build();
    }

    /**
     * Remove token do GitHub.
     */
    @DeleteMapping("/github/token")
    public ResponseEntity<Void> clearGitHubToken(@CurrentUser String userLogin) {
        clearGitHubTokenUseCase.execute(userLogin);
        return ResponseEntity.ok().build();
    }

    // ============= MÉTODOS AUXILIARES PARA COOKIES =============

    private void setAuthenticationCookies(HttpServletResponse response, AuthResponse authResponse) {
        // Access token - curta duração (15 minutos)
        addCookie(response, "token", authResponse.token(), 15 * 60);

        // Refresh token - longa duração (7 dias)
        addCookie(response, "refreshToken", authResponse.refreshToken(), 7 * 24 * 60 * 60);
    }

    private void clearAuthenticationCookies(HttpServletResponse response) {
        clearCookie(response, "token");
        clearCookie(response, "refreshToken");
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
