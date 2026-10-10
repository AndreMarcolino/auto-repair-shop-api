package br.com.autorepairshop.domain.cliente;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;

public class Cliente {

	private UUID id;
	private String nome;
	private CpfCnpj cpfCnpj;
	private String telefone;
	private Email email;

	private boolean ativo;
	private LocalDateTime criadoEm;
	private LocalDateTime atualizadoEm;

	private Cliente(UUID id, String nome, CpfCnpj cpfCnpj, String telefone, Email email, boolean ativo,
			LocalDateTime criadoEm, LocalDateTime atualizadoEm) {

		this.id = Objects.requireNonNull(id);
		this.nome = Objects.requireNonNull(nome);
		this.cpfCnpj = Objects.requireNonNull(cpfCnpj);
		this.telefone = telefone;
		this.email = email;
		this.ativo = ativo;
		this.criadoEm = criadoEm;
		this.atualizadoEm = atualizadoEm;
	}

	private static final Predicate<String> NOME_INVALIDO = nome -> nome == null || nome.isBlank();

	public static Cliente criar(String nome, CpfCnpj cpfCnpj, String telefone, Email email) {

		if (NOME_INVALIDO.test(nome)) {
			throw new DomainException(DomainError.NOME_CLIENTE_INVALIDO);
		}

		if (cpfCnpj == null) {
			throw new DomainException(DomainError.CPF_CNPJ_INVALIDO);
		}

		if (email == null) {
			throw new DomainException(DomainError.EMAIL_INVALIDO);
		}

		LocalDateTime agora = LocalDateTime.now();

		return new Cliente(UUID.randomUUID(), nome.trim(), cpfCnpj, telefone, email, true, agora, agora);

	 
	}

	public Cliente alterar(String nome, CpfCnpj cpfCnpj, String telefone, Email email) {

		if (cpfCnpj == null) {
			throw new DomainException(DomainError.CPF_CNPJ_INVALIDO);
		}

		if (email == null) {
			throw new DomainException(DomainError.EMAIL_INVALIDO);
		}

		this.nome = nome.trim();
		this.cpfCnpj = cpfCnpj;
		this.telefone = telefone;
		this.email = email;
		this.atualizadoEm = LocalDateTime.now();

		return this;
	}

	public void desativar() {
		if (!ativo) {
			throw new DomainException(DomainError.CLIENTE_JA_DESATIVADO);
		}

		ativo = false;
		atualizadoEm = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public CpfCnpj getCpfCnpj() {
		return cpfCnpj;
	}

	public String getTelefone() {
		return telefone;
	}

	public Email getEmail() {
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

	public static Cliente reconstituir(UUID id, String nome, CpfCnpj cpfCnpj, String telefone, Email email,
			boolean ativo, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {

		return new Cliente(id, nome, cpfCnpj, telefone, email, ativo, criadoEm, atualizadoEm);
	}
}
