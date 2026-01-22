package bragdoc.domain.user;

/**
 * Interface de serviço de domínio para operações OAuth.
 * Implementação fica na infraestrutura.
 */
public interface OAuthService {

    /**
     * Troca o código de autorização por um access token.
     */
    String exchangeCodeForToken(String code, String redirectUri);

    /**
     * Busca o perfil do usuário usando o access token.
     */
    GitHubProfile getUserProfile(String accessToken);
}
