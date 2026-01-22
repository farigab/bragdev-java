package bragdoc.interfaces.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.infrastructure.security.JwtService;
import bragdoc.interfaces.api.common.CurrentUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolver customizado para a anotação @CurrentUser.
 * Extrai o JWT do cookie e retorna o login do usuário.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    public CurrentUserArgumentResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new UnauthorizedException("Request inválido");
        }

        String jwt = extractJwtFromCookie(request);
        if (jwt == null || jwt.isBlank()) {
            throw new UnauthorizedException("Token não encontrado");
        }

        String userLogin = jwtService.extractUserLogin(jwt);
        if (userLogin == null || userLogin.isBlank()) {
            throw new UnauthorizedException("Token inválido");
        }

        return userLogin;
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
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
}
