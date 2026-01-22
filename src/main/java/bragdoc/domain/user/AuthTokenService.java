package bragdoc.domain.user;

import java.util.Map;

/**
 * Interface de serviço de domínio para geração de tokens de autenticação.
 * Implementação fica na infraestrutura.
 */
public interface AuthTokenService {

    /**
     * Gera um token JWT para o usuário.
     */
    String generateToken(String userLogin, Map<String, Object> claims);

    /**
     * Extrai o login do usuário de um token.
     */
    String extractUserLogin(String token);

    /**
     * Valida se um token é válido.
     */
    boolean isValid(String token);
}
