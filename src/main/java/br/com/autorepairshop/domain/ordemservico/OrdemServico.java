package br.com.autorepairshop.domain.ordemservico;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class OrdemServico {

    private final UUID id;
    private final UUID clienteId;
    private final UUID veiculoId;
    private final LocalDateTime dataAbertura;

    private StatusOrdemServico status;
    private LocalDateTime dataInicioServico;
    private LocalDateTime dataFimServico;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataEntrega;
    private String problemaRelatado;

    private OrdemServico(
            UUID id,
            UUID clienteId,
            UUID veiculoId,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataInicioServico,
            LocalDateTime dataFimServico,
            LocalDateTime dataPagamento,
            LocalDateTime dataEntrega,
            String problemaRelatado) {

        this.id = id;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataInicioServico = dataInicioServico;
        this.dataFimServico = dataFimServico;
        this.dataPagamento = dataPagamento;
        this.dataEntrega = dataEntrega;
        this.problemaRelatado = problemaRelatado;
    }

    public static OrdemServico abrir(
            UUID clienteId,
            UUID veiculoId,
            String problemaRelatado) {

        if (clienteId == null) {
           throw new DomainException(DomainError.CLIENTE_OBRIGATORIO_ABERTURA_OS);
        }

        if (veiculoId == null) {
        	  throw new DomainException(DomainError.VEICULO_OBRIGATORIO_ABERTURA_OS);
        }
        
        if(problemaRelatado == null || problemaRelatado.isBlank()) {
        	 throw new DomainException(DomainError.RELATO_DO_PROBLEMA_OBRIGATORIO_ABERTURA_OS);
        }
        
        if(problemaRelatado.length() > 1000) {
       	 throw new DomainException(DomainError.RELATO_DO_PROBLEMA_INVALIDO);
       }

        return new OrdemServico(
            UUID.randomUUID(),
            clienteId,
            veiculoId,
            StatusOrdemServico.RECEBIDA,
            LocalDateTime.now(),
            null,
            null,
            null,
            null,
            problemaRelatado
        );
    }

    public static OrdemServico recriaAgregado(
            UUID id,
            UUID clienteId,
            UUID veiculoId,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataInicioServico,
            LocalDateTime dataFimServico,
            LocalDateTime dataPagamento,
            LocalDateTime dataEntrega,
            String problemaRelatado) {

        return new OrdemServico(
            id,
            clienteId,
            veiculoId,
            status,
            dataAbertura,
            dataInicioServico,
            dataFimServico,
            dataPagamento,
            dataEntrega,
            problemaRelatado
        );
    }

    public void iniciarDiagnostico() {
    	if(!this.status.equals(StatusOrdemServico.RECEBIDA)) {
    		throw new DomainException(DomainError.INICIO_DIAGNOSTICO_NAO_PERMITIDO);
    	}
        transicionarPara(StatusOrdemServico.EM_DIAGNOSTICO);
    }

    public void aguardarEnvioOrcamento() {
    	if(!this.status.equals(StatusOrdemServico.EM_DIAGNOSTICO)) {
    		throw new DomainException(DomainError.STATUS_INVALIDO_PARA_GERACAO_ORCAMENTO);
    	}
        transicionarPara(
            StatusOrdemServico.AGUARDANDO_ENVIO_ORCAMENTO
        );
    }

    public void aguardarAprovacao() {
    	if(!this.status.equals(StatusOrdemServico.AGUARDANDO_ENVIO_ORCAMENTO)) {
    		throw new DomainException(DomainError.STATUS_INVALIDO_PARA_ENVIO_ORCAMENTO);
    	}
        transicionarPara(
            StatusOrdemServico.AGUARDANDO_APROVACAO
        );
    }

    public void aguardarPecas() {
        transicionarPara(StatusOrdemServico.AGUARDANDO_PECAS);
    }

    public void iniciarExecucao() {
        transicionarPara(StatusOrdemServico.EM_EXECUCAO);
    }

    public void finalizar() {
        transicionarPara(StatusOrdemServico.FINALIZADA);
    }

    public void entregar() {
        transicionarPara(StatusOrdemServico.ENTREGUE);
    }

    public void registrarPagamento() {
        if (dataPagamento != null) {
            throw new IllegalStateException(
                "O pagamento já foi registrado."
            );
        }

        dataPagamento = LocalDateTime.now();
    }

    private void transicionarPara(StatusOrdemServico novoStatus) {
        boolean transicaoPermitida = switch (status) {
            case RECEBIDA ->
                novoStatus == StatusOrdemServico.EM_DIAGNOSTICO;

            case EM_DIAGNOSTICO ->
                novoStatus ==
                    StatusOrdemServico.AGUARDANDO_ENVIO_ORCAMENTO;

            case AGUARDANDO_ENVIO_ORCAMENTO ->
                novoStatus ==
                    StatusOrdemServico.AGUARDANDO_APROVACAO;

            case AGUARDANDO_APROVACAO ->
                novoStatus == StatusOrdemServico.AGUARDANDO_PECAS
                || novoStatus == StatusOrdemServico.EM_EXECUCAO;

            case AGUARDANDO_PECAS ->
                novoStatus == StatusOrdemServico.EM_EXECUCAO;

            case EM_EXECUCAO ->
                novoStatus == StatusOrdemServico.FINALIZADA;

            case FINALIZADA ->
                novoStatus == StatusOrdemServico.ENTREGUE;

            case ENTREGUE -> false;
        };

        if (!transicaoPermitida) {
            throw new IllegalStateException(
                "Transição inválida de " + status
                    + " para " + novoStatus
            );
        }

        LocalDateTime agora = LocalDateTime.now();

        switch (novoStatus) {
            case EM_EXECUCAO ->
                dataInicioServico = agora;

            case FINALIZADA ->
                dataFimServico = agora;

            case ENTREGUE ->
                dataEntrega = agora;

            default -> {
                // Nenhuma data adicional a registrar.
            }
        }

        status = novoStatus;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataInicioServico() {
        return dataInicioServico;
    }

    public LocalDateTime getDataFimServico() {
        return dataFimServico;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

	public String getProblemaRelatado() {
		return problemaRelatado;
	}
    
    
    
    
    
    
}