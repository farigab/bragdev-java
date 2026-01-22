package bragdoc.domain.user;

import bragdoc.domain.shared.exceptions.DomainException;
import java.util.Objects;

/**
 * Entidade de domínio User - representa um usuário do sistema.
 * Livre de frameworks e anotações.
 */
public class User {
    private final String login;
    private String name;
    private String avatarUrl;
    private GitHubToken githubToken;

    private User(String login, String name, String avatarUrl, GitHubToken githubToken) {
        this.login = validateLogin(login);
        this.name = validateName(name);
        this.avatarUrl = avatarUrl;
        this.githubToken = githubToken;
    }

    public static User create(String login, String name, String avatarUrl) {
        return new User(login, name, avatarUrl, null);
    }

    public static User restore(String login, String name, String avatarUrl, String githubTokenValue) {
        GitHubToken token = githubTokenValue != null ? GitHubToken.of(githubTokenValue) : null;
        return new User(login, name, avatarUrl, token);
    }

    public void updateProfile(String name, String avatarUrl) {
        this.name = validateName(name);
        this.avatarUrl = avatarUrl;
    }

    public void setGitHubToken(String tokenValue) {
        this.githubToken = GitHubToken.of(tokenValue);
    }

    public void clearGitHubToken() {
        this.githubToken = null;
    }

    public boolean hasGitHubToken() {
        return githubToken != null;
    }

    private String validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new DomainException("Login é obrigatório");
        }
        if (login.length() > 100) {
            throw new DomainException("Login não pode ter mais de 100 caracteres");
        }
        return login;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Nome é obrigatório");
        }
        return name;
    }

    // Getters
    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getGithubTokenValue() {
        return githubToken != null ? githubToken.getValue() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User user))
            return false;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
