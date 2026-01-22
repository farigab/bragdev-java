package bragdoc.interfaces.api.auth;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
import bragdoc.application.user.SaveGitHubTokenUseCase;
import bragdoc.application.user.dto.SaveGitHubTokenRequest;
import bragdoc.interfaces.api.common.CurrentUser;
import bragdoc.interfaces.api.user.dto.SaveGitHubTokenApiRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controller para autenticação e OAuth.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação e OAuth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final SaveGitHubTokenUseCase saveGitHubTokenUseCase;
    private final ClearGitHubTokenUseCase clearGitHubTokenUseCase;

    @Value("${github.oauth.client-id}")
    private String clientId;

    @Value("${github.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${github.oauth.frontend-redirect-uri}")
    private String frontendRedirectUri;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            SaveGitHubTokenUseCase saveGitHubTokenUseCase,
            ClearGitHubTokenUseCase clearGitHubTokenUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.saveGitHubTokenUseCase = saveGitHubTokenUseCase;
        this.clearGitHubTokenUseCase = clearGitHubTokenUseCase;
    }

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

    @GetMapping("/callback")
    public void callback(
            @RequestParam String code,
            HttpServletResponse response) {
        try {
            var authResponse = authenticateUserUseCase.execute(code, redirectUri);

            var cookie = ResponseCookie.from("token", authResponse.token())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("None")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            response.sendRedirect(frontendRedirectUri + "/dashboard");

        } catch (Exception e) {
            try {
                response.sendRedirect(frontendRedirectUri + "/login?error=auth_failed");
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao redirecionar", ex);
            }
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/github/token")
    public ResponseEntity<Void> saveGitHubToken(
            @Valid @RequestBody SaveGitHubTokenApiRequest apiRequest,
            @CurrentUser String userLogin) {

        var request = new SaveGitHubTokenRequest(apiRequest.token());
        saveGitHubTokenUseCase.execute(request, userLogin);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/github/token")
    public ResponseEntity<Void> clearGitHubToken(@CurrentUser String userLogin) {
        clearGitHubTokenUseCase.execute(userLogin);
        return ResponseEntity.ok().build();
    }
}
