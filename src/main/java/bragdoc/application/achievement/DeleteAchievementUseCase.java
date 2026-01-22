package bragdoc.application.achievement;

import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;

public class DeleteAchievementUseCase {

    private final AchievementRepository repository;

    public DeleteAchievementUseCase(AchievementRepository repository) {
        this.repository = repository;
    }

    public void execute(Long id, String userLogin) {
        Achievement achievement = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achievement não encontrado"));

        if (!achievement.belongsToUser(userLogin)) {
            throw new UnauthorizedException("Acesso negado");
        }

        repository.delete(achievement);
    }
}
