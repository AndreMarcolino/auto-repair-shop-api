package br.com.autorepairshop.domain.shared;

public enum DomainError {

	NOME_OBRIGATORIO("CLI-001", "Nome do cliente é obrigatório."),
    TIPO_PESSOA_INVALIDO("CLI-002", "Tipo de pessoa inválido."),
    CPF_CNPJ_INVALIDO("CLI-003", "CPF/CNPJ inválido."),
    CPF_CNPJ_DUPLICADO("CLI-004", "CPF/CNPJ já cadastrado."),
	EMAIL_INVALIDO("CLI_005","Email inválido"),
	NOME_CLIENTE_INVALIDO("CLI_006","Nome do cliente obrigatório"),
	CLIENTE_NAO_ENCONTRADO("CLI_007","Cliente não encontrado"),
	CLIENTE_ID_INVALIDO("CLI_008", "ID Cliente inválido"),
	CLIENTE_JA_DESATIVO("CLI_009", "Cliente já está desativado"),
	TIPO_PESSOA_OBRIGATORIO_PARA_CPF_CNPJ("CLI_010", "Tipo pessoal obrigatório para CPF/CNPJ");

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