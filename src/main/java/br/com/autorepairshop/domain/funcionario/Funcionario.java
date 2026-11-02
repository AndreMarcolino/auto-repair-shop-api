package br.com.autorepairshop.domain.funcionario;

import java.time.Instant;
import java.util.UUID;

public class Funcionario {

    private final UUID id;
    private String nome;
    private String email;
    private String senhaHash;
    private final TipoFuncionario tipo;
    private final Instant criadoEm;
    private boolean ativo;

    private Funcionario(
            UUID id,
            String nome,
            String email,
            String senhaHash,
            TipoFuncionario tipo,
            Instant criadoEm,
            boolean ativo) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
        this.criadoEm = criadoEm;
        this.ativo = ativo;
    }

    public static Funcionario recria(
            UUID id,
            String nome,
            String email,
            String senhaHash,
            TipoFuncionario tipo,
            Instant criadoEm,
            boolean ativo) {

        return new Funcionario(
                id,
                nome,
                email,
                senhaHash,
                tipo,
                criadoEm,
                ativo
        );
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public TipoFuncionario getTipo() {
        return tipo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void alterarSenha(String senhaHash) {
        this.senhaHash = senhaHash;
    }
    
    public boolean podeAtenderExecutarServico() {
        return ativo && tipo == TipoFuncionario.MECANICO;
    }
    
    public boolean podeBaixarPagamento() {
        return ativo && tipo == TipoFuncionario.ATENDENTE;
    }
}