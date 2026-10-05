package br.com.autorepairshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 11)
    private String cpf;

    @Column(length = 14)
    private String cnpj;

    protected Customer() {
        // Required by JPA
    }

    public Customer(String name, String cpf, String cnpj) {
        this.name = name;
        this.cpf = cpf;
        this.cnpj = cnpj;
    }

    public void update(String name, String cpf, String cnpj) {
        this.name = name;
        this.cpf = cpf;
        this.cnpj = cnpj;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCnpj() {
        return cnpj;
    }
}