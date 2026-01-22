package bragdoc.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para User.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(unique = true, nullable = false, length = 100)
    private String login;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "github_access_token", length = 500)
    private String githubAccessToken;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(String login, String name, String avatarUrl, String githubAccessToken) {
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.githubAccessToken = githubAccessToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGithubAccessToken() {
        return githubAccessToken;
    }

    public void setGithubAccessToken(String githubAccessToken) {
        this.githubAccessToken = githubAccessToken;
    }
}
