package bragdoc.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bragdoc.infrastructure.persistence.jpa.entity.UserJpaEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByLogin(String login);

    boolean existsByLogin(String login);
}
