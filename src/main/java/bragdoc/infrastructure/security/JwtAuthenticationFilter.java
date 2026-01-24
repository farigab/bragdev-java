package bragdoc.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de autenticação JWT.
 *
 * Responsabilidades:
 * - Extrair JWT do cookie
 * - Validar JWT
 * - Configurar contexto de segurança do Spring
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.trace("JwtAuthenticationFilter - URI: {}", uri);

        String token = (String) request.getAttribute("REFRESHED_ACCESS_TOKEN");

        if (token != null) {
            log.debug("Usando token renovado pelo filtro anterior");
        } else {
            token = extractTokenFromCookie(request);
            log.debug("Token extraído do cookie: {}", token != null ? "presente" : "ausente");
        }

        if (token != null && jwtTokenService.isValid(token)) {
            log.debug("Token válido, autenticando usuário");
            authenticateUser(token, request);
        } else {
            log.debug("Token inválido ou ausente - usuário NÃO autenticado");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void authenticateUser(String token, HttpServletRequest request) {
        try {
            String userLogin = jwtTokenService.extractUserLogin(token);

            if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userLogin,
                        null,
                        Collections.emptyList());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {

        }
    }
}
