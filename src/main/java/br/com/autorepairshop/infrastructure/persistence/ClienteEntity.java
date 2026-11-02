package br.com.autorepairshop.infrastructure.persistence;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Embedded
    private CpfCnpjEmbeddable cpfCnpj;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Embedded
    private EmailEmbeddable email;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    protected ClienteEntity() {
    }

    public static ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();

        entity.id = cliente.getId();
        entity.nome = cliente.getNome();
        entity.cpfCnpj = new CpfCnpjEmbeddable(
                cliente.getCpfCnpj().tipoPessoa(),
                cliente.getCpfCnpj().cpfCnpj());
        entity.telefone = cliente.getTelefone();
        entity.email = new EmailEmbeddable(cliente.getEmail().email());
        entity.ativo = cliente.isAtivo();
        entity.criadoEm = cliente.getCriadoEm();
        entity.atualizadoEm = cliente.getAtualizadoEm();

        return entity;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CpfCnpjEmbeddable getCpfCnpj() {
        return cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public EmailEmbeddable getEmail() {
        return email;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public Cliente toDomain() {
        return Cliente.recriaAgregado(
                id,
                nome,
                new CpfCnpj(
                        cpfCnpj.getTipoPessoa(),
                        cpfCnpj.getCpfCnpj()),
                telefone,
                new Email(email.getEmail()),
                ativo,
                criadoEm,
                atualizadoEm);
    }
}