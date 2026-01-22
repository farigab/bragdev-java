package bragdoc.application.achievement;

import bragdoc.application.achievement.dto.AchievementResponse;
import bragdoc.application.achievement.dto.CreateAchievementRequest;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;

public class CreateAchievementUseCase {

    private final AchievementRepository repository;

    public CreateAchievementUseCase(AchievementRepository repository) {
        this.repository = repository;
    }

    public AchievementResponse execute(CreateAchievementRequest request, String userLogin) {
        Achievement achievement = Achievement.create(
                request.title(),
                request.description(),
                request.category(),
                request.date(),
                userLogin);

        Achievement saved = repository.save(achievement);
        return AchievementResponse.from(saved);
    }
}
