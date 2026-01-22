package bragdoc.application.github;

import java.util.List;

import bragdoc.domain.github.GitHubClient;
import bragdoc.domain.github.GitHubRepository;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Listar repositórios do GitHub do usuário.
 */
public class ListRepositoriesUseCase {

    private final UserRepository userRepository;
    private final GitHubClient gitHubClient;

    public ListRepositoriesUseCase(UserRepository userRepository, GitHubClient gitHubClient) {
        this.userRepository = userRepository;
        this.gitHubClient = gitHubClient;
    }

    public List<String> execute(String userLogin) {
        // 1. Buscar usuário
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // 2. Verificar se tem token do GitHub
        if (!user.hasGitHubToken()) {
            throw new UnauthorizedException("Token do GitHub não configurado");
        }

        // 3. Buscar repositórios
        List<GitHubRepository> repositories = gitHubClient.listRepositories(
                user.getGithubTokenValue());

        // 4. Retornar apenas os nomes completos
        return repositories.stream()
                .map(GitHubRepository::fullName)
                .toList();
    }
}
