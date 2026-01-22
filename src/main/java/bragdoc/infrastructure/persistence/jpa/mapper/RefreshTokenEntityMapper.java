package bragdoc.infrastructure.persistence.jpa.mapper;

import bragdoc.domain.user.RefreshToken;
import bragdoc.infrastructure.persistence.jpa.entity.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre RefreshToken domain e RefreshToken JPA entity.
 */
@Component
public class RefreshTokenEntityMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return RefreshToken.restore(
                entity.getToken(),
                entity.getUserLogin(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.isRevoked());
    }

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }

        return new RefreshTokenJpaEntity(
                domain.getToken(),
                domain.getUserLogin(),
                domain.getExpiresAt(),
                domain.getCreatedAt(),
                domain.isRevoked());
    }
}
