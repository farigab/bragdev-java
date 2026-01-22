package bragdoc.application.achievement;

import bragdoc.application.achievement.dto.AchievementResponse;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;

import java.time.LocalDate;
import java.util.List;

public class FindAchievementUseCase {

    private final AchievementRepository repository;

    public FindAchievementUseCase(AchievementRepository repository) {
        this.repository = repository;
    }

    public AchievementResponse findById(Long id, String userLogin) {
        Achievement achievement = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achievement não encontrado"));

        if (!achievement.belongsToUser(userLogin)) {
            throw new UnauthorizedException("Acesso negado");
        }

        return AchievementResponse.from(achievement);
    }

    public List<AchievementResponse> findAll(String userLogin) {
        return repository.findByUser(userLogin)
                .stream()
                .map(AchievementResponse::from)
                .toList();
    }

    public List<AchievementResponse> findByCategory(String category, String userLogin) {
        return repository.findByUserAndCategory(userLogin, category)
                .stream()
                .map(AchievementResponse::from)
                .toList();
    }

    public List<AchievementResponse> findByDateRange(LocalDate startDate, LocalDate endDate,
            String userLogin) {
        return repository.findByUserAndDateRange(userLogin, startDate, endDate)
                .stream()
                .map(AchievementResponse::from)
                .toList();
    }

    public List<AchievementResponse> searchByTitle(String keyword, String userLogin) {
        return repository.findByUserAndTitleContaining(userLogin, keyword)
                .stream()
                .map(AchievementResponse::from)
                .toList();
    }
}
