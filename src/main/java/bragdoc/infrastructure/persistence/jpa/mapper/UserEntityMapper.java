package bragdoc.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import bragdoc.domain.user.User;
import bragdoc.infrastructure.persistence.jpa.entity.UserJpaEntity;

/**
 * Mapper entre User domain e User JPA entity.
 */
@Component
public class UserEntityMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.restore(
                entity.getLogin(),
                entity.getName(),
                entity.getAvatarUrl(),
                entity.getGithubAccessToken());
    }

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return new UserJpaEntity(
                domain.getLogin(),
                domain.getName(),
                domain.getAvatarUrl(),
                domain.getGithubTokenValue());
    }
}
