package bragdoc.domain.github;

/**
 * Value Object representando um repositório do GitHub.
 */
public record GitHubRepository(String fullName, String owner, String name) {

    public GitHubRepository {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Nome do repositório é obrigatório");
        }

        if (!fullName.contains("/")) {
            throw new IllegalArgumentException(
                    "Nome do repositório deve estar no formato 'owner/repo'");
        }
    }

    public static GitHubRepository from(String fullName) {
        String[] parts = fullName.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato inválido: esperado 'owner/repo'");
        }
        return new GitHubRepository(fullName, parts[0], parts[1]);
    }

    public static GitHubRepository of(String owner, String name) {
        return new GitHubRepository(owner + "/" + name, owner, name);
    }
}
