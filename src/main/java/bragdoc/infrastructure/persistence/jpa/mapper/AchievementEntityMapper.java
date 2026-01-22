package bragdoc.infrastructure.persistence.jpa.mapper;

import bragdoc.domain.achievement.Achievement;
import bragdoc.infrastructure.persistence.jpa.entity.AchievementJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidade de domínio e entidade JPA.
 * Isola as camadas.
 */
@Component
public class AchievementEntityMapper {

    public Achievement toDomain(AchievementJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Achievement.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getDate(),
                entity.getUserLogin());
    }

    public AchievementJpaEntity toJpaEntity(Achievement domain) {
        if (domain == null) {
            return null;
        }

        return new AchievementJpaEntity(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getCategory().getValue(),
                domain.getDate(),
                domain.getUserLogin());
    }
}
