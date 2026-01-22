package bragdoc.domain.user;

import java.util.Objects;

import bragdoc.domain.shared.exceptions.DomainException;

/**
 * Value Object representando um token de acesso do GitHub.
 */
public class GitHubToken {
    private final String value;

    private GitHubToken(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Token do GitHub não pode ser vazio");
        }
        this.value = value;
    }

    public static GitHubToken of(String value) {
        return new GitHubToken(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GitHubToken that))
            return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
