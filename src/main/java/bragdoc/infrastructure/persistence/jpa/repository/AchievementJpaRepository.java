package bragdoc.infrastructure.persistence.jpa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bragdoc.infrastructure.persistence.jpa.entity.AchievementJpaEntity;

/**
 * Spring Data JPA Repository - parte da infraestrutura
 */
@Repository
public interface AchievementJpaRepository extends JpaRepository<AchievementJpaEntity, Long> {

    List<AchievementJpaEntity> findByUserLogin(String userLogin);

    List<AchievementJpaEntity> findByUserLoginAndCategory(String userLogin, String category);

    List<AchievementJpaEntity> findByUserLoginAndDateBetween(String userLogin,
            LocalDate startDate,
            LocalDate endDate);

    List<AchievementJpaEntity> findByUserLoginAndTitleContainingIgnoreCase(String userLogin,
            String keyword);

    boolean existsByUserLoginAndDateAndTitle(String userLogin, LocalDate date, String title);
}
