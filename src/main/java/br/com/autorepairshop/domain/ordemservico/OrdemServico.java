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
    	this.status = StatusOrdemServico.EM_DIAGNOSTICO;
    }

    public void enviarParaAprovacaoCliente() {
        if (status != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new DomainException(
                DomainError.STATUS_INVALIDO_PARA_ENVIO_ORCAMENTO
            );
        }

        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
    }

    public void aprovar() {
        if (status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new DomainException(
                DomainError.ORDEM_SERVICO_NAO_PODE_SER_APROVADA
            );
        }

        this.status = StatusOrdemServico.APROVADA;
        
    }
    
    public void rejeitar() {
        if (status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new DomainException(
                DomainError.ORDEM_SERVICO_NAO_PODE_SER_APROVADA
            );
        }

        this.status = StatusOrdemServico.REJEITADA;
        
    }
    
    public void iniciarServico() {
    	 if (status != StatusOrdemServico.APROVADA) {
             throw new DomainException(
                 DomainError.ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_APROVADA
             );
         }

         this.status = StatusOrdemServico.EM_EXECUCAO;
         this.dataInicioServico = LocalDateTime.now();
    }
    
    public void finalizarServico() {
   	 if (status != StatusOrdemServico.EM_EXECUCAO) {
            throw new DomainException(
                DomainError.ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_APROVADA
            );
        }

        this.status = StatusOrdemServico.FINALIZADA;
        this.dataFimServico = LocalDateTime.now();
   }

    public void aguardarPagamento() {
      	 if (status != StatusOrdemServico.FINALIZADA) {
               throw new DomainException(
                   DomainError.ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_FINALIZADA
               );
           }

           this.status = StatusOrdemServico.AGUARDANDO_PAGAMENTO;
      }
    public void aguardarInsumos() {
        if (status != StatusOrdemServico.APROVADA) {
            throw new DomainException(
                DomainError.ORDEM_SERVICO_AGUARDANDO_INSUMOS
            );
        }

        this.status = StatusOrdemServico.AGUARDANDO_PECAS;
    }

    
    public void registrarPagamento() 
	{
	   	 if (status != StatusOrdemServico.AGUARDANDO_PAGAMENTO) {
	            throw new DomainException(
	                DomainError.ORDEM_SERVICO_TEM_QUE_ESTAR_COM_STATUS_AGUARDANDO_PAGAMENTO
	            );
	        }

	        this.status = StatusOrdemServico.PAGA;
	        this.dataPagamento = LocalDateTime.now();
	 }
    
    public void entregarVeiculo() 
	{
	   	 if (status != StatusOrdemServico.PAGA) {
	            throw new DomainException(
	                DomainError.ORDEM_SERVICO_TEM_QUE_ESTAR_PAGA
	            );
	        }

	        this.status = StatusOrdemServico.ENTREGUE;
	        this.dataPagamento = LocalDateTime.now();
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