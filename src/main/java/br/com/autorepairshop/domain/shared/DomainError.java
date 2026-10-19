package br.com.autorepairshop.domain.shared;

import br.com.autorepairshop.domain.orcamento.StatusOrcamento;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;

public enum DomainError {

    NOME_CLIENTE_OBRIGATORIO("DOM-001", "Nome do cliente é obrigatório."),
    TIPO_PESSOA_INVALIDO("DOM-002", "Tipo de pessoa inválido."),
    CPF_CNPJ_INVALIDO("DOM-003", "CPF/CNPJ inválido."),
    CPF_CNPJ_JA_CADASTRADO("DOM-004", "CPF/CNPJ já cadastrado."),
    EMAIL_INVALIDO("DOM-005", "Email inválido"),
    NOME_CLIENTE_INVALIDO("DOM-006", "Nome do cliente obrigatório"),
    CLIENTE_NAO_ENCONTRADO("DOM-007", "Cliente não encontrado"),
    ID_CLIENTE_INVALIDO("DOM-008", "Código do Cliente inválido"),
    CLIENTE_JA_DESATIVADO("DOM-009", "Cliente já está desativado"),
    TIPO_PESSOA_OBRIGATORIO_PARA_CPF_CNPJ("DOM-010", "Tipo pessoal obrigatório para CPF/CNPJ"),
    PLACA_INVALIDA("DOM-011", "Placa veículo inválida"),
    MARCA_VEICULO_INVALIDA("DOM-012", "Marca veículo inválida"),
    MODELO_VEICULO_INVALIDO("DOM-013", "Modelo veículo inválido"),
    ANO_FABRICACAO_INVALIDO("DOM-014", "Ano fabricação veículo inválido"),
    PLACA_VEICULO_JA_CADASTRADA("DOM-015", "Placa já cadastrada"),
    ID_VEICULO_INVALIDO("DOM-016", "Código do Veículo inválido"),
    VEICULO_NAO_ENCONTRADO("DOM-017", "Veículo não encontrado"),
    DESCRICAO_INSUMO_OBRIGATORIA("DOM-018", "Descrição do insumo obrigatória"),
    VALOR_REFERENCIA_INSUMO_INVALIDO("DOM-019", "Valor do insumo inválido"),
    QUANTIDADE_ESTOQUE_INVALIDA("DOM-020", "Quantidade em estoque inválida"),
    DESCRICAO_SERVICO_OBRIGATORIA("DOM-021", "Descrição do serviço obrigatória"),
    VALOR_REFERENCIA_SERVICO_INVALIDO("DOM-022", "Valor do serviço inválido"),
    ID_SERVICO_INVALIDO("DOM-023", "Código do serviço inválido"),
    SERVICO_NAO_ENCONTRADO("DOM-024", "Serviço não encontrado"),
    PAGINACAO_NEGATIVA("DOM-025", "A página não pode ser negativa"),
    TAMANHO_PAGINA("DOM-026", "O tamanho da página deve ser maior que zero."),
    ID_INSUMO_INVALIDO("DOM-027", "Código do insumo inválido"),
    INSUMO_NAO_ENCONTRADO("DOM-028", "Insumo não encontrado"),
    CLIENTE_DESATIVADO_ABERTURA_OS("DOM-029", "Não é possível abrir Ordem de serviço para um cliente desativado"),
    VEICULO_OBRIGATORIO_ABERTURA_OS("DOM-030", "Veículo obrigatório para abertura de OS"),
    CLIENTE_OBRIGATORIO_ABERTURA_OS("DOM-031", "Cliente obrigatório para abertura de OS"),
    RELATO_DO_PROBLEMA_OBRIGATORIO_ABERTURA_OS("DOM-032", "Relato do problema obrigatório para abertura OS"),
    RELATO_DO_PROBLEMA_INVALIDO("DOM-033", "O problema relatado deve ter no máximo 1.000 caracteres"),
    ID_ORDEM_SERVICO_INVALIDO("DOM-034", "Id da Ordem de Serviço inválido"),
    OS_NAO_ENCONTRADA("DOM-035", "Ordem de Serviço não encontrada"),
    INICIO_DIAGNOSTICO_NAO_PERMITIDO("DOM-036", "Ordem de serviço deve estar com o status " + StatusOrdemServico.RECEBIDA),
    STATUS_INVALIDO_PARA_GERACAO_ORCAMENTO("DOM-037", "Ordem de serviço deve estar com o status " + StatusOrdemServico.EM_DIAGNOSTICO + " para geração do orçamento"),
    STATUS_INVALIDO_PARA_ENVIO_ORCAMENTO("DOM-039", "Ordem de serviço deve estar com o status " + StatusOrdemServico.EM_DIAGNOSTICO + " para envio de orçamento"),
	INSUMOS_NAO_ENCONTRADOS("DOM-39", "Insumos não encontrados %s"),
    ORDEM_SERVICO_OBRIGATORIA("DOM-040", "Ordem de Serviço obrigatória"),
	INFORME_AO_MENOS_UM_INSUMO("DOM-041", "Informe ao menos um insumo"),
	IDS_INSUMOS_NAO_PODEM_SER_NULOS_OU_VAZIO("DOM-042","Os IDs dos insumos não podem ser nulos ou vazios."),
	ID_DO_USUARIO_OBRIGATORIO("DOM-043","Os IDs dos insumos não podem ser nulos ou vazios."),
	ORCAMENTO_NAO_ENCONTRADO("DOM-044", "Orçamento não encontrado"),
	ORCAMENTO_NAO_ESTA_EM_ELABORACAO("DOM-045", "Orçamento não está em elaboração"),
	ORCAMENTO_NAO_ESTA_EM_AGUARDADO_APROVACAO("DOM-046", "O orçamento não está em aguardando aprovação."),
	INFORME_AO_MENOS_UM_SERVICO("DOM-047", "Informe ao menos um insumo"),
	IDS_SERVICOS_NAO_PODEM_SER_NULOS_OU_VAZIO("DOM-048","Os IDs dos serviços não podem ser nulos ou vazios."),
	SERVICOS_NAO_ENCONTRADOS("DOM-049", "Insumos não encontrados %s"),
	DIAGNOSTICO_OBRIGATORIO("DOM-50","Informe o diagnóstico"),
	ORCAMENTO_DEVE_POSSUIR_AO_MENOS_UM_SERVICO("DOM-051","Orçamento deve possiur ao menos um serviço"),
	ID_MECANICO_INVALIDO("DOM-052", "Id do Mecânico inválido"),
	MECANICO_NAO_ENCONTRADO("DOM-053", "Mecânico não encontrado"),
	FUNCIONARIO_SEM_PERMISSAO("DOM-054","Funcionário sem permissão"),
	ORDEM_SERVICO_NAO_ENCONTRADA("DOM-055", "Ordem de Serviço não encontrada"),
	ORDEM_SERVICO_NAO_PODE_SER_APROVADA("DOM-056","Ordem de serviço não pode ser aprovada"),
	ORDEM_SERVICO_AGUARDANDO_INSUMOS("DOM-057","Ordem de serviço aguardando insumos"),
	ORCAMENTO_NAO_PODE_SER_APROVADO("DOM-058","Orcamento não pode ser aprovado"),
	ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE("DOM-059","Ordem de serviço não pertence ao cliente"),
	ORCAMENTO_NAO_PODE_SER_REPROVADO("DOM-060","Orcamento não pode ser reprovado"),
	ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_APROVADA("DOM-061","Ordem de serviço tem que estar com o status aprovada"),
	ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_FINALIZADA("DOM-062","Ordem de serviço tem que estar com o status finalizada"),
	ID_ATENDENTE_INVALIDO("DOM-063", "Id do Atendente inválido"),
	ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_AGUARDANDO_PAGAMENTO("DOM-064","Ordem de serviço tem que estar aguardando pagamento"),
	ORDEM_SERVICO_TEM_QUE_ESTAR_PAGA("DOM-064","Ordem de serviço tem que estar com o status paga");
	
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

    public String getMensagem(Object... parametros) {
        return parametros.length == 0
                ? mensagem
                : String.format(mensagem, parametros);
    }
}