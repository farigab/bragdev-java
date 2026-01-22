package bragdoc.application.user;

import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Limpar token do GitHub de um usuário.
 */
public class ClearGitHubTokenUseCase {

    private final UserRepository userRepository;

    public ClearGitHubTokenUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String userLogin) {
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.clearGitHubToken();
        userRepository.save(user);
    }
}
