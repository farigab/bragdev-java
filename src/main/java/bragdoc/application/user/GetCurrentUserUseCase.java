package bragdoc.application.user;

import bragdoc.application.user.dto.UserResponse;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Obter usuário atual autenticado.
 */
public class GetCurrentUserUseCase {

    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(String userLogin) {
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return UserResponse.from(user);
    }
}
