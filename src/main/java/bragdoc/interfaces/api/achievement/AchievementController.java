package bragdoc.interfaces.api.achievement;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bragdoc.application.achievement.CreateAchievementUseCase;
import bragdoc.application.achievement.DeleteAchievementUseCase;
import bragdoc.application.achievement.FindAchievementUseCase;
import bragdoc.application.achievement.UpdateAchievementUseCase;
import bragdoc.application.achievement.dto.CreateAchievementRequest;
import bragdoc.application.achievement.dto.UpdateAchievementRequest;
import bragdoc.interfaces.api.achievement.dto.AchievementApiResponse;
import bragdoc.interfaces.api.achievement.dto.CreateAchievementApiRequest;
import bragdoc.interfaces.api.achievement.dto.UpdateAchievementApiRequest;
import bragdoc.interfaces.api.common.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST - camada de interface.
 * Responsável apenas por:
 * - Validar entrada HTTP
 * - Mapear DTOs
 * - Delegar para Use Cases
 * - Retornar respostas HTTP
 */
@RestController
@RequestMapping("/api/achievements")
@Tag(name = "Conquistas", description = "Operações relacionadas às conquistas profissionais")
public class AchievementController {

    private final CreateAchievementUseCase createUseCase;
    private final UpdateAchievementUseCase updateUseCase;
    private final DeleteAchievementUseCase deleteUseCase;
    private final FindAchievementUseCase findUseCase;

    public AchievementController(
            CreateAchievementUseCase createUseCase,
            UpdateAchievementUseCase updateUseCase,
            DeleteAchievementUseCase deleteUseCase,
            FindAchievementUseCase findUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.findUseCase = findUseCase;
    }

    @PostMapping
    public ResponseEntity<AchievementApiResponse> create(
            @Valid @RequestBody CreateAchievementApiRequest apiRequest,
            @CurrentUser String userLogin) {

        var request = new CreateAchievementRequest(
                apiRequest.title(),
                apiRequest.description(),
                apiRequest.category(),
                apiRequest.date());

        var response = createUseCase.execute(request, userLogin);
        var apiResponse = AchievementApiResponse.from(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAchievementApiRequest apiRequest,
            @CurrentUser String userLogin) {

        var request = new UpdateAchievementRequest(
                apiRequest.title(),
                apiRequest.description(),
                apiRequest.category(),
                apiRequest.date());

        var response = updateUseCase.execute(id, request, userLogin);
        var apiResponse = AchievementApiResponse.from(response);

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @CurrentUser String userLogin) {

        deleteUseCase.execute(id, userLogin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementApiResponse> findById(
            @PathVariable Long id,
            @CurrentUser String userLogin) {

        var response = findUseCase.findById(id, userLogin);
        var apiResponse = AchievementApiResponse.from(response);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<List<AchievementApiResponse>> findAll(
            @CurrentUser String userLogin) {

        var responses = findUseCase.findAll(userLogin);
        var apiResponses = responses.stream()
                .map(AchievementApiResponse::from)
                .toList();

        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<AchievementApiResponse>> findByCategory(
            @PathVariable String category,
            @CurrentUser String userLogin) {

        var responses = findUseCase.findByCategory(category, userLogin);
        var apiResponses = responses.stream()
                .map(AchievementApiResponse::from)
                .toList();

        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AchievementApiResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CurrentUser String userLogin) {

        var responses = findUseCase.findByDateRange(startDate, endDate, userLogin);
        var apiResponses = responses.stream()
                .map(AchievementApiResponse::from)
                .toList();

        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AchievementApiResponse>> searchByTitle(
            @RequestParam String keyword,
            @CurrentUser String userLogin) {

        var responses = findUseCase.searchByTitle(keyword, userLogin);
        var apiResponses = responses.stream()
                .map(AchievementApiResponse::from)
                .toList();

        return ResponseEntity.ok(apiResponses);
    }
}
