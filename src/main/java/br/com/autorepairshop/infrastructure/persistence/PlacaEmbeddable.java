package br.com.autorepairshop.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlacaEmbeddable {

    @Column(name = "placa", length = 7, nullable = false)
    private String placa;

    protected PlacaEmbeddable() {
    }

    public PlacaEmbeddable(String placa) {
        this.placa = placa;
    }

    public String getPlaca() {
        return placa;
    }
}
