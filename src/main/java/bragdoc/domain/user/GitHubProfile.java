package bragdoc.domain.user;

/**
 * Value Object representando dados do perfil do GitHub.
 */
public record GitHubProfile(
        String login,
        String name,
        String avatarUrl,
        String email) {
    public GitHubProfile {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login é obrigatório");
        }
    }
}
