package bragdoc.domain.achievement;

import java.time.LocalDate;
import java.util.Objects;

import bragdoc.domain.shared.exceptions.DomainException;

/**
 * Entidade de domínio Achievement - representa uma conquista profissional.
 * Não possui dependências de frameworks.
 */
public class Achievement {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private LocalDate date;
    private String userLogin;

    private Achievement(Long id, String title, String description, Category category,
                       LocalDate date, String userLogin) {
        this.id = id;
        setTitle(title);
        setDescription(description);
        setCategory(category);
        setDate(date);
        setUserLogin(userLogin);
    }

    public static Achievement create(String title, String description, String category,
                                    LocalDate date, String userLogin) {
        return new Achievement(null, title, description, Category.from(category), date, userLogin);
    }

    public static Achievement restore(Long id, String title, String description, String category,
                                     LocalDate date, String userLogin) {
        return new Achievement(id, title, description, Category.from(category), date, userLogin);
    }

    public void update(String title, String description, String category, LocalDate date) {
        setTitle(title);
        setDescription(description);
        setCategory(Category.from(category));
        setDate(date);
    }

    public boolean belongsToUser(String login) {
        return Objects.equals(this.userLogin, login);
    }

    private void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Título é obrigatório");
        }
        if (title.length() > 255) {
            throw new DomainException("Título não pode ter mais de 255 caracteres");
        }
        this.title = title;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setCategory(Category category) {
        if (category == null) {
            throw new DomainException("Categoria é obrigatória");
        }
        this.category = category;
    }

    private void setDate(LocalDate date) {
        if (date == null) {
            throw new DomainException("Data é obrigatória");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new DomainException("Data não pode ser futura");
        }
        this.date = date;
    }

    private void setUserLogin(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            throw new DomainException("Usuário é obrigatório");
        }
        this.userLogin = userLogin;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getUserLogin() { return userLogin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
