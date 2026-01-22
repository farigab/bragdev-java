package bragdoc.domain.user;

import java.util.Optional;

/**
 * Interface do repositório de User - definida no domínio.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    void delete(User user);
}
