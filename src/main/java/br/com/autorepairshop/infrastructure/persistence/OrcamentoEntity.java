package br.com.autorepairshop.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.autorepairshop.domain.orcamento.StatusOrcamento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "orcamento",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_orcamento_ordem_servico",
        columnNames = "ordem_servico_id"
    ),
    indexes = @Index(
        name = "idx_orcamento_mecanico_id",
        columnList = "mecanico_id"
    )
)
public class OrcamentoEntity {

    @Id
    private UUID id;

    @Column(name = "ordem_servico_id", nullable = false, unique = true)
    private UUID ordemServicoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private FuncionarioEntity mecanico;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrcamento status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @OneToMany(
        mappedBy = "orcamento",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrcamentoItemInsumoEntity> pecas = new ArrayList<>();

    @OneToMany(
        mappedBy = "orcamento",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrcamentoItemServicoEntity> servicos = new ArrayList<>();

    protected OrcamentoEntity() {
        // Exigido pelo JPA
    }

    public OrcamentoEntity(
            UUID id,
            UUID ordemServicoId,
            UUID mecanicoId,
            String diagnostico,
            StatusOrcamento status,
            LocalDateTime criadoEm,
            LocalDateTime enviadoEm) {

        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.mecanico = new FuncionarioEntity(mecanicoId);
        this.diagnostico = diagnostico;
        this.status = status;
        this.criadoEm = criadoEm;
        this.enviadoEm = enviadoEm;
    }

    public void adicionarInsumo(OrcamentoItemInsumoEntity item) {
        pecas.add(item);
        item.definirOrcamento(this);
    }

    public void removerPeca(OrcamentoItemInsumoEntity item) {
        pecas.remove(item);
        item.definirOrcamento(null);
    }

    public void adicionarServico(OrcamentoItemServicoEntity item) {
        servicos.add(item);
        item.definirOrcamento(this);
    }

    public void removerServico(OrcamentoItemServicoEntity item) {
        servicos.remove(item);
        item.definirOrcamento(null);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrdemServicoId() {
        return ordemServicoId;
    }

    public FuncionarioEntity getMecanico() {
        return mecanico;
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

    public List<OrcamentoItemInsumoEntity> getInsumos() {
        return List.copyOf(pecas);
    }

    public List<OrcamentoItemServicoEntity> getServicos() {
        return List.copyOf(servicos);
    }
    
    
}