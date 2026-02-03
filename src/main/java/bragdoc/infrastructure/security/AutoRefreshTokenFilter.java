package bragdoc.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import bragdoc.application.user.RefreshAccessTokenUseCase;
import bragdoc.application.user.dto.AuthResponse;

import java.io.IOException;

/**
 * Filtro de renovação automática de tokens.
 *
 * Responsabilidades:
 * - Detectar quando access token está ausente/expirado
 * - Tentar renovar usando refresh token automaticamente
 * - Adicionar novos tokens nos cookies
 *
 * Este filtro roda ANTES do JwtAuthenticationFilter.
 * Se conseguir renovar, o próximo filtro terá um token válido.
 */
@Slf4j
@Component
@Order(1)
public class AutoRefreshTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    public AutoRefreshTokenFilter(
            JwtTokenService jwtTokenService,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase) {
        this.jwtTokenService = jwtTokenService;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.trace("AutoRefreshTokenFilter - URI: {}", uri);

        if (isPublicEndpoint(uri)) {
            log.trace("Endpoint público. Pulando filtro");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractCookie(request, "token");
        String refreshToken = extractCookie(request, "refreshToken");

        log.debug("Cookies presentes - Access Token: {}, Refresh Token: {}", 
                accessToken != null ? "SIM" : "NÃO", 
                refreshToken != null ? "SIM" : "NÃO");

        if (accessToken != null && jwtTokenService.isValid(accessToken)) {
            log.debug("Access token válido - continuando sem renovação");
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Access token ausente ou inválido");

        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("Refresh token ausente - usuário precisa fazer login");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Refresh token presente - tentando renovação automática");

        boolean refreshed = tryAutoRefresh(refreshToken, request, response);

        if (refreshed) {
            log.info("Token renovado automaticamente com sucesso");
        } else {
            log.error("Falha ao renovar token - limpando cookies e retornando 401");
            clearAuthCookies(response);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Tenta renovar o access token usando o refresh token.
     * Retorna true se conseguiu renovar com sucesso.
     */
    private boolean tryAutoRefresh(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Tentando renovar access token usando refresh token");
            AuthResponse authResponse = refreshAccessTokenUseCase.execute(refreshToken);

            setAuthCookies(response, authResponse);

            request.setAttribute("REFRESHED_ACCESS_TOKEN", authResponse.token());

            log.info("Token renovado com sucesso para usuário: {}", authResponse.user().login());
            return true;
        } catch (Exception e) {
            log.error("Falha ao renovar token automaticamente: {} - {}", 
                    e.getClass().getSimpleName(), 
                    e.getMessage());
            return false;
        }
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs");
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        // Access token - 15 minutos
        addCookie(response, "token", authResponse.token(), 15 * 60);

        // Refresh token - 7 dias
        addCookie(response, "refreshToken", authResponse.refreshToken(), 7 * 24 * 60 * 60);
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

    private void clearAuthCookies(HttpServletResponse response) {
        addCookie(response, "token", "", 0);
        addCookie(response, "refreshToken", "", 0);
    }
}
