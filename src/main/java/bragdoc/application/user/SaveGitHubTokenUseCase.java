package bragdoc.application.user;

import bragdoc.application.user.dto.SaveGitHubTokenRequest;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Salvar token do GitHub para um usuário.
 */
public class SaveGitHubTokenUseCase {

    private final UserRepository userRepository;

    public SaveGitHubTokenUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(SaveGitHubTokenRequest request, String userLogin) {
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setGitHubToken(request.token());
        userRepository.save(user);
    }
}
