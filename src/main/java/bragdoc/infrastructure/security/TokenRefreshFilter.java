package bragdoc.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import bragdoc.domain.user.RefreshToken;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

import java.io.IOException;
import java.util.Map;

@Component
@Order(1)
public class TokenRefreshFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenRefreshFilter(
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Pula endpoints públicos
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractTokenFromCookie(request, "token");
        String refreshTokenValue = extractTokenFromCookie(request, "refreshToken");

        // Se access token está expirado/ausente mas há refresh token válido
        boolean accessTokenInvalid = accessToken == null || jwtService.isExpired(accessToken);

        if (accessTokenInvalid && refreshTokenValue != null) {
            try {
                // Buscar refresh token no banco
                RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                        .orElse(null);

                if (refreshToken != null && refreshToken.isValid()) {
                    // Buscar usuário
                    User user = userRepository.findByLogin(refreshToken.getUserLogin())
                            .orElse(null);

                    if (user != null) {
                        // Gerar novo access token
                        String newAccessToken = jwtService.generateAccessToken(
                                user.getLogin(),
                                Map.of(
                                        "name", user.getName(),
                                        "avatar", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""));

                        // Adicionar cookie com novo token
                        addAccessTokenCookie(response, newAccessToken);

                        // Armazenar no request para o próximo filtro
                        request.setAttribute("renewed_access_token", newAccessToken);
                    }
                }
            } catch (Exception e) {
                // Se falhar, limpa cookies
                clearAuthCookies(response);
            }
        } else if (accessTokenInvalid) {
            // Ambos inválidos, limpa cookies
            clearAuthCookies(response);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void addAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60); // 15 minutos
        response.addCookie(cookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        response.addCookie(tokenCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
    }
}
