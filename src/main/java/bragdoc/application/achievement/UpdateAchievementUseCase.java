package bragdoc.application.achievement;

import bragdoc.application.achievement.dto.AchievementResponse;
import bragdoc.application.achievement.dto.UpdateAchievementRequest;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;

public class UpdateAchievementUseCase {

    private final AchievementRepository repository;

    public UpdateAchievementUseCase(AchievementRepository repository) {
        this.repository = repository;
    }

    public AchievementResponse execute(Long id, UpdateAchievementRequest request, String userLogin) {
        Achievement achievement = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achievement não encontrado"));

        if (!achievement.belongsToUser(userLogin)) {
            throw new UnauthorizedException("Acesso negado");
        }

        achievement.update(
                request.title(),
                request.description(),
                request.category(),
                request.date());

        Achievement updated = repository.save(achievement);
        return AchievementResponse.from(updated);
    }
}
