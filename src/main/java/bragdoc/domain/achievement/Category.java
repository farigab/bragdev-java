package bragdoc.domain.achievement;

import java.util.Objects;

import bragdoc.domain.shared.exceptions.DomainException;

/**
 * Value Object representando a categoria de uma conquista.
 */
public class Category {
    private final String value;

    private Category(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Categoria não pode ser vazia");
        }
        this.value = value.trim();
    }

    public static Category from(String value) {
        return new Category(value);
    }

    public String getValue() {
        return value;
    }

    public boolean isGitHub() {
        return value.startsWith("GitHub");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Category category))
            return false;
        return Objects.equals(value, category.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
