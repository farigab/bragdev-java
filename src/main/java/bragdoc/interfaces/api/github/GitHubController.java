package bragdoc.interfaces.api.github;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bragdoc.application.github.ImportGitHubDataUseCase;
import bragdoc.application.github.ListRepositoriesUseCase;
import bragdoc.application.github.dto.ImportGitHubDataRequest;
import bragdoc.application.github.dto.ImportGitHubDataResponse;
import bragdoc.interfaces.api.common.CurrentUser;
import bragdoc.interfaces.api.github.dto.ImportGitHubDataApiRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller para operações de integração com GitHub.
 */
@RestController
@RequestMapping("/api/github")
@Tag(name = "GitHub", description = "Integração com GitHub para conquistas")
public class GitHubController {

    private final ListRepositoriesUseCase listRepositoriesUseCase;
    private final ImportGitHubDataUseCase importGitHubDataUseCase;

    public GitHubController(
            ListRepositoriesUseCase listRepositoriesUseCase,
            ImportGitHubDataUseCase importGitHubDataUseCase) {
        this.listRepositoriesUseCase = listRepositoriesUseCase;
        this.importGitHubDataUseCase = importGitHubDataUseCase;
    }

    @PostMapping("/import/repositories")
    public ResponseEntity<List<String>> listRepositories(@CurrentUser String userLogin) {
        List<String> repositories = listRepositoriesUseCase.execute(userLogin);
        return ResponseEntity.ok(repositories);
    }

    @PostMapping("/import")
    public ResponseEntity<ImportGitHubDataResponse> importData(
            @Valid @RequestBody ImportGitHubDataApiRequest apiRequest,
            @CurrentUser String userLogin) {

        var request = new ImportGitHubDataRequest(
                apiRequest.repositories(),
                apiRequest.dataInicio(),
                apiRequest.dataFim());

        var response = importGitHubDataUseCase.execute(request, userLogin);
        return ResponseEntity.ok(response);
    }
}
