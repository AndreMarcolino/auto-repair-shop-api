package br.com.autorepairshop.domain.shared;

public enum DomainError {

	NOME_CLIENTE_OBRIGATORIO("DOM-001", "Nome do cliente é obrigatório."),
    TIPO_PESSOA_INVALIDO("DOM-002", "Tipo de pessoa inválido."),
    CPF_CNPJ_INVALIDO("DOM-003", "CPF/CNPJ inválido."),
    CPF_CNPJ_JA_CADASTRADO("DOM-004", "CPF/CNPJ já cadastrado."),
	EMAIL_INVALIDO("DOM-005","Email inválido"),
	NOME_CLIENTE_INVALIDO("DOM-006","Nome do cliente obrigatório"),
	CLIENTE_NAO_ENCONTRADO("DOM-007","Cliente não encontrado"),
	ID_CLIENTE_INVALIDO("DOM-008", "Código do Cliente inválido"),
	CLIENTE_JA_DESATIVADO("DOM-009", "Cliente já está desativado"),
	TIPO_PESSOA_OBRIGATORIO_PARA_CPF_CNPJ("DOM-010", "Tipo pessoal obrigatório para CPF/CNPJ"),
	PLACA_INVALIDA("DOM-011", "Placa veículo inválida"),
	MARCA_VEICULO_INVALIDA("DOM-012", "Marca veículo inválida"),
	MODELO_VEICULO_INVALIDO("DOM-013","Modelo veículo inválido"),
	ANO_FABRICACAO_INVALIDO("DOM-014","Ano fabricação veículo inválido"),
	PLACA_VEICULO_JA_CADASTRADA("DOM-015","Placa já cadastrada"),
	ID_VEICULO_INVALIDO("DOM-016", "Código do Veículo inválido"),
	VEICULO_NAO_ENCONTRADO("DOM-017","Veículo não encontrado");
	
    private final String codigo;
    private final String mensagem;

    DomainError(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }
}