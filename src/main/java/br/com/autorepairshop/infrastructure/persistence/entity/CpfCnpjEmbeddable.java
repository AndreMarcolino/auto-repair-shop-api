package br.com.autorepairshop.infrastructure.persistence.entity;

import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CpfCnpjEmbeddable {

	@Column(name = "cpf_cnpj_tipo_pessoa", nullable = false)
	private Short tipoPessoa;

	@Column(name = "cpf_cnpj", nullable = false, length = 14)
	private String cpfCnpj;

    protected CpfCnpjEmbeddable() {
    }

    public CpfCnpjEmbeddable(TipoPessoa tipoPessoa, String cpfCnpj) {
        this.tipoPessoa = tipoPessoa.getCodigo();
        this.cpfCnpj = cpfCnpj;
    }

    public TipoPessoa getTipoPessoa() {
        return TipoPessoa.deCodigo(tipoPessoa);
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }
}