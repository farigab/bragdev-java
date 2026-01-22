package bragdoc.interfaces.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bragdoc.application.user.GetCurrentUserUseCase;
import bragdoc.interfaces.api.common.CurrentUser;
import bragdoc.interfaces.api.user.dto.UserApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para operações relacionadas ao usuário atual.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public UserController(GetCurrentUserUseCase getCurrentUserUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @GetMapping
    public ResponseEntity<UserApiResponse> getCurrentUser(@CurrentUser String userLogin) {
        var response = getCurrentUserUseCase.execute(userLogin);
        var apiResponse = UserApiResponse.from(response);
        return ResponseEntity.ok(apiResponse);
    }
}
