package bragdoc.infrastructure.integration.github;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import bragdoc.domain.user.GitHubProfile;
import bragdoc.domain.user.OAuthService;

/**
 * Implementação do serviço OAuth usando a API do GitHub.
 */
@Service
public class GitHubOAuthService implements OAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${github.oauth.client-id}")
    private String clientId;

    @Value("${github.oauth.client-secret}")
    private String clientSecret;

    public GitHubOAuthService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String exchangeCodeForToken(String code, String redirectUri) {
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            var body = Map.of(
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "code", code,
                    "redirect_uri", redirectUri);

            var request = new HttpEntity<>(body, headers);
            var uri = URI.create("https://github.com/login/oauth/access_token");

            var response = restTemplate.postForEntity(uri, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Falha ao trocar código por token");
            }

            var responseBody = response.getBody();

            // Tenta JSON primeiro
            try {
                var node = objectMapper.readTree(responseBody);
                if (node.has("access_token")) {
                    return node.get("access_token").asText();
                }
            } catch (Exception e) {
                // Fallback para form-encoded
                for (var part : responseBody.split("&")) {
                    if (part.startsWith("access_token=")) {
                        return part.substring("access_token=".length());
                    }
                }
            }

            throw new RuntimeException("Token não encontrado na resposta");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao trocar código por token", e);
        }
    }

    @Override
    public GitHubProfile getUserProfile(String accessToken) {
        try {
            var headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            var request = new HttpEntity<Void>(headers);
            var uri = URI.create("https://api.github.com/user");

            var response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Falha ao buscar perfil do usuário");
            }

            var node = objectMapper.readTree(response.getBody());

            String login = node.get("login").asText();
            String name = node.has("name") && !node.get("name").asText().isBlank()
                    ? node.get("name").asText()
                    : login;
            String avatarUrl = node.has("avatar_url")
                    ? node.get("avatar_url").asText()
                    : null;
            String email = node.has("email") && !node.get("email").isNull()
                    ? node.get("email").asText()
                    : null;

            return new GitHubProfile(login, name, avatarUrl, email);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar perfil do usuário", e);
        }
    }
}
