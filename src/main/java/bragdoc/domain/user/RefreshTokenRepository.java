package bragdoc.domain.user;

import java.util.Optional;

/**
 * Interface do repositório de RefreshToken - definida no domínio.
 * A implementação fica na camada de infraestrutura.
 */
public interface RefreshTokenRepository {

    /**
     * Salva ou atualiza um refresh token.
     */
    RefreshToken save(RefreshToken refreshToken);

    /**
     * Busca um refresh token pelo valor do token.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Busca todos os tokens de um usuário.
     */
    java.util.List<RefreshToken> findByUserLogin(String userLogin);

    /**
     * Deleta um refresh token.
     */
    void delete(RefreshToken refreshToken);

    /**
     * Deleta todos os tokens de um usuário.
     */
    void deleteAllByUserLogin(String userLogin);

    /**
     * Deleta todos os tokens expirados.
     */
    void deleteExpiredTokens();
}
