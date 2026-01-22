package bragdoc.domain.achievement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de Achievement - definida no domínio.
 * A implementação fica na camada de infraestrutura.
 */
public interface AchievementRepository {

    Achievement save(Achievement achievement);

    Optional<Achievement> findById(Long id);

    List<Achievement> findByUser(String userLogin);

    List<Achievement> findByUserAndCategory(String userLogin, String category);

    List<Achievement> findByUserAndDateRange(String userLogin, LocalDate startDate, LocalDate endDate);

    List<Achievement> findByUserAndTitleContaining(String userLogin, String keyword);

    void delete(Achievement achievement);

    boolean existsByUserAndDateAndTitle(String userLogin, LocalDate date, String title);
}
