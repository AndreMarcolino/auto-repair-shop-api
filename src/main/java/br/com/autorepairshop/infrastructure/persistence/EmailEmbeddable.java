package br.com.autorepairshop.infrastructure.persistence;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EmailEmbeddable {

    @Column(name = "email", length = 254)
    private String email;

    protected EmailEmbeddable() {
    }

    public EmailEmbeddable(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
