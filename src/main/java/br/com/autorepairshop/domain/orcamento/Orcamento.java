package br.com.autorepairshop.domain.orcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;

public class Orcamento {

	private final UUID id;
	private final UUID ordemServicoId;
	private final UUID mecanicoId;
	private final LocalDateTime criadoEm;

	private String diagnostico;
	private StatusOrcamento status;
	private LocalDateTime enviadoEm;

	private final List<OrcamentoItemInsumo> insumos = new ArrayList<>();
	private final List<OrcamentoItemServico> servicos = new ArrayList<>();

	private Orcamento(UUID ordemServicoId, UUID mecanicoId) {
		this.id = UUID.randomUUID();
		this.ordemServicoId = Objects.requireNonNull(ordemServicoId);
		this.mecanicoId = Objects.requireNonNull(mecanicoId);
		this.criadoEm = LocalDateTime.now();
		this.status = StatusOrcamento.EM_ELABORACAO;
	}

	private Orcamento(UUID id, UUID ordemServicoId, UUID mecanicoId, String diagnostico, StatusOrcamento status,
			LocalDateTime criadoEm, LocalDateTime enviadoEm) {

		this.id = Objects.requireNonNull(id);
		this.ordemServicoId = Objects.requireNonNull(ordemServicoId);
		this.mecanicoId = Objects.requireNonNull(mecanicoId);
		this.diagnostico = diagnostico;
		this.status = Objects.requireNonNull(status);
		this.criadoEm = Objects.requireNonNull(criadoEm);
		this.enviadoEm = enviadoEm;
	}

	private Orcamento(UUID id, UUID ordemServicoId, UUID mecanicoId, String diagnostico, StatusOrcamento status,
			LocalDateTime criadoEm, LocalDateTime enviadoEm,
		   List<OrcamentoItemInsumo> insumos,
		   List<OrcamentoItemServico> servicos) {

		this.id = Objects.requireNonNull(id);
		this.ordemServicoId = Objects.requireNonNull(ordemServicoId);
		this.mecanicoId = Objects.requireNonNull(mecanicoId);
		this.diagnostico = diagnostico;
		this.status = Objects.requireNonNull(status);
		this.criadoEm = Objects.requireNonNull(criadoEm);
		this.enviadoEm = enviadoEm;
		this.insumos.addAll(insumos);
		this.servicos.addAll(servicos);
	}
	public static Orcamento criar(UUID ordemServicoId, UUID mecanicoId) {

		return new Orcamento(ordemServicoId, mecanicoId);
	}

	public void finalizarElaboracao(String diagnostico) {
	    if (status != StatusOrcamento.EM_ELABORACAO) {
	        throw new DomainException(
	            DomainError.ORCAMENTO_NAO_ESTA_EM_ELABORACAO
	        );
	    }

	    if (diagnostico == null || diagnostico.isBlank()) {
	        throw new DomainException(
	            DomainError.DIAGNOSTICO_OBRIGATORIO
	        );
	    }
	    
	    if (servicos.isEmpty()) {
	        throw new DomainException(
	            DomainError.ORCAMENTO_DEVE_POSSUIR_AO_MENOS_UM_SERVICO
	        );
	    }


	    this.diagnostico = diagnostico;
	    this.enviadoEm = LocalDateTime.now();
	    status = StatusOrcamento.AGUARDANDO_APROVACAO;
	}
	
	public void aprovar() {
	    if (status != StatusOrcamento.AGUARDANDO_APROVACAO) {
	        throw new DomainException(
	            DomainError.ORCAMENTO_NAO_PODE_SER_APROVADO
	        );
	    }

	    this.status = StatusOrcamento.APROVADO;
	    
	}
	
	public void reprovar() {
	    if (status != StatusOrcamento.AGUARDANDO_APROVACAO) {
	        throw new DomainException(
	            DomainError.ORCAMENTO_NAO_PODE_SER_APROVADO
	        );
	    }

	    this.status = StatusOrcamento.REPROVADO;
	    
	}
	

	public void adicionarInsumo(
	        UUID insumoId,
	        String descricao,
	        BigDecimal quantidade,
	        BigDecimal valorUnitario) {

	    var itemExistente = insumos.stream()
	            .filter(item -> item.getInsumoId().equals(insumoId))
	            .findFirst();

	    if (itemExistente.isPresent()) {
	        itemExistente.get().atualizarQuantidade(quantidade);
	        return;
	    }

	    insumos.add(new OrcamentoItemInsumo(
	            insumoId,
	            descricao,
	            quantidade,
	            valorUnitario
	    ));
	}

	public void adicionarServico(
	        UUID servicoId,
	        String descricao,
	        BigDecimal quantidade,
	        BigDecimal valorUnitario) {

	    var itemExistente = servicos.stream()
	            .filter(item -> item.getServicoId().equals(servicoId))
	            .findFirst();

	    if (itemExistente.isPresent()) {
	        itemExistente.get().atualizarQuantidade(quantidade);
	        return;
	    }

	    servicos.add(new OrcamentoItemServico(
	            servicoId,
	            descricao,
	            quantidade,
	            valorUnitario
	    ));
	}

	
	public BigDecimal calcularTotalInsumos() {
		return insumos.stream().map(OrcamentoItemInsumo::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal calcularTotalServicos() {
		return servicos.stream().map(OrcamentoItemServico::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal calcularValorTotal() {
		return calcularTotalInsumos().add(calcularTotalServicos());
	}

	
	public UUID getId() {
		return id;
	}

	public UUID getOrdemServicoId() {
		return ordemServicoId;
	}

	public UUID getMecanicoId() {
		return mecanicoId;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public StatusOrcamento getStatus() {
		return status;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public LocalDateTime getEnviadoEm() {
		return enviadoEm;
	}

	public List<OrcamentoItemInsumo> getInsumos() {
		return List.copyOf(insumos);
	}

	public List<OrcamentoItemServico> getServicos() {
		return List.copyOf(servicos);
	}

	
	public static Orcamento recriaAgregado(UUID id, UUID ordemServicoId, UUID mecanicoId, String diagnostico,
			StatusOrcamento status, LocalDateTime criadoEm, LocalDateTime enviadoEm) {

		return new Orcamento(id, ordemServicoId, mecanicoId, diagnostico, status, criadoEm, enviadoEm);
	}
	public static Orcamento recriaAgregado(UUID id, UUID ordemServicoId, UUID mecanicoId, String diagnostico,
			StatusOrcamento status, LocalDateTime criadoEm, LocalDateTime enviadoEm, List<OrcamentoItemInsumo> insumos, List<OrcamentoItemServico> servicos) {

		return new Orcamento(id, ordemServicoId, mecanicoId, diagnostico, status, criadoEm, enviadoEm, insumos, servicos);
	}
}
