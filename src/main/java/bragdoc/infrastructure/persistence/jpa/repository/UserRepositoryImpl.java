package bragdoc.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;
import bragdoc.infrastructure.persistence.jpa.entity.UserJpaEntity;
import bragdoc.infrastructure.persistence.jpa.mapper.UserEntityMapper;

/**
 * Implementação JPA do repositório de User.
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jpaRepository.findByLogin(login)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }

    @Override
    public void delete(User user) {
        jpaRepository.deleteById(user.getLogin());
    }
}
