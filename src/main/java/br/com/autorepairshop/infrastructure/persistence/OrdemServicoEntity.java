package br.com.autorepairshop.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.StatusOrdemServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordem_servico")
public class OrdemServicoEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "status_id", nullable = false)
    private Short status;

    @Column(
        name = "data_abertura",
        nullable = false,
        updatable = false
    )
    private LocalDateTime dataAbertura;

    @Column(name = "data_inicio_servico")
    private LocalDateTime dataInicioServico;

    @Column(name = "data_fim_servico")
    private LocalDateTime dataFimServico;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;
    
    @Column(name = "problema_relatado", length = 1000)
    private String problemaRelatado;

    protected OrdemServicoEntity() {
       
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(UUID veiculoId) {
        this.veiculoId = veiculoId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataInicioServico() {
        return dataInicioServico;
    }

    public void setDataInicioServico(LocalDateTime dataInicioServico) {
        this.dataInicioServico = dataInicioServico;
    }

    public LocalDateTime getDataFimServico() {
        return dataFimServico;
    }

    public void setDataFimServico(LocalDateTime dataFimServico) {
        this.dataFimServico = dataFimServico;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
    
    
    public String getProblemaRelatado() {
		return problemaRelatado;
	}

	public void setProblemaRelatado(String problemaRelatado) {
		this.problemaRelatado = problemaRelatado;
	}

	public OrdemServico toDomain() {
        return OrdemServico.recriaAgregado(
            id,
            clienteId,
            veiculoId,
            StatusOrdemServico.porId(status),
            dataAbertura,
            dataInicioServico,
            dataFimServico,
            dataPagamento,
            dataEntrega,
            problemaRelatado
        );
    }
    
    public static OrdemServicoEntity toEntity(OrdemServico ordemServico) {
        OrdemServicoEntity entity = new OrdemServicoEntity();

        entity.id = ordemServico.getId();
        entity.clienteId = ordemServico.getClienteId();
        entity.veiculoId = ordemServico.getVeiculoId();
        entity.status = ordemServico.getStatus().getId();
        entity.dataAbertura = ordemServico.getDataAbertura();
        entity.dataInicioServico = ordemServico.getDataInicioServico();
        entity.dataFimServico = ordemServico.getDataFimServico();
        entity.dataPagamento = ordemServico.getDataPagamento();
        entity.dataEntrega = ordemServico.getDataEntrega();
        entity.problemaRelatado = ordemServico.getProblemaRelatado();

        return entity;
    }
}