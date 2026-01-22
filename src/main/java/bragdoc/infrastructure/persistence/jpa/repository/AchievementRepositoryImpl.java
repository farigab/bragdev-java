package bragdoc.infrastructure.persistence.jpa.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.infrastructure.persistence.jpa.entity.AchievementJpaEntity;
import bragdoc.infrastructure.persistence.jpa.mapper.AchievementEntityMapper;

/**
 * Implementação do repositório de domínio usando JPA.
 * Faz a ponte entre domínio e infraestrutura.
 */
@Component
public class AchievementRepositoryImpl implements AchievementRepository {

    private final AchievementJpaRepository jpaRepository;
    private final AchievementEntityMapper mapper;

    public AchievementRepositoryImpl(AchievementJpaRepository jpaRepository,
            AchievementEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Achievement save(Achievement achievement) {
        AchievementJpaEntity entity = mapper.toJpaEntity(achievement);
        AchievementJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Achievement> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Achievement> findByUser(String userLogin) {
        return jpaRepository.findByUserLogin(userLogin)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Achievement> findByUserAndCategory(String userLogin, String category) {
        return jpaRepository.findByUserLoginAndCategory(userLogin, category)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Achievement> findByUserAndDateRange(String userLogin, LocalDate startDate,
            LocalDate endDate) {
        return jpaRepository.findByUserLoginAndDateBetween(userLogin, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Achievement> findByUserAndTitleContaining(String userLogin, String keyword) {
        return jpaRepository.findByUserLoginAndTitleContainingIgnoreCase(userLogin, keyword)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Achievement achievement) {
        jpaRepository.deleteById(achievement.getId());
    }

    @Override
    public boolean existsByUserAndDateAndTitle(String userLogin, LocalDate date, String title) {
        return jpaRepository.existsByUserLoginAndDateAndTitle(userLogin, date, title);
    }
}
