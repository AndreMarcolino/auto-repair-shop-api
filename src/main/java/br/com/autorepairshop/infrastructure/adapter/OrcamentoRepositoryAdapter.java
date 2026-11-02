package br.com.autorepairshop.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoItemInsumo;
import br.com.autorepairshop.domain.orcamento.OrcamentoItemServico;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.infrastructure.persistence.OrcamentoEntity;
import br.com.autorepairshop.infrastructure.persistence.OrcamentoItemInsumoEntity;
import br.com.autorepairshop.infrastructure.persistence.OrcamentoItemServicoEntity;
import br.com.autorepairshop.infrastructure.repository.OrcamentoJpaRepository;

@Repository
public class OrcamentoRepositoryAdapter
        implements OrcamentoRepository {

    private final OrcamentoJpaRepository jpaRepository;

    public OrcamentoRepositoryAdapter(
            OrcamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(Orcamento orcamento) {
        OrcamentoEntity entity = toEntity(orcamento);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Orcamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Orcamento> buscarPorOrdemServicoId(
            UUID ordemServicoId) {

        return jpaRepository
                .findByOrdemServicoId(ordemServicoId)
                .map(this::toDomain);
    }

    private OrcamentoEntity toEntity(Orcamento orcamento) {
        OrcamentoEntity entity = new OrcamentoEntity(
                orcamento.getId(),
                orcamento.getOrdemServicoId(),
                orcamento.getMecanicoId(),
                orcamento.getDiagnostico(),
                orcamento.getStatus(),
                orcamento.getCriadoEm(),
                orcamento.getEnviadoEm()
        );

        orcamento.getInsumos().forEach(item ->
                entity.adicionarInsumo(
                        new OrcamentoItemInsumoEntity(
                                item.getId(),
                                item.getInsumoId(),
                                item.getDescricao(),
                                item.getQuantidade(),
                                item.getValorUnitario()
                        )
                )
        );

        orcamento.getServicos().forEach(item ->
                entity.adicionarServico(
                        new OrcamentoItemServicoEntity(
                                item.getId(),
                                item.getServicoId(),
                                item.getDescricao(),
                                item.getQuantidade(),
                                item.getValorUnitario()
                        )
                )
        );

        return entity;
    }

    private Orcamento toDomain(OrcamentoEntity entity) {
        List<OrcamentoItemInsumo> insumos = entity.getInsumos()
                .stream()
                .map(OrcamentoItemInsumoEntity::toDomain)
                .toList();

        List<OrcamentoItemServico> servicos = entity.getServicos()
                .stream()
                .map(OrcamentoItemServicoEntity::toDomain)
                .toList();

        return Orcamento.recriaAgregado(
                entity.getId(),
                entity.getOrdemServicoId(),
                entity.getMecanico().getId(),
                entity.getDiagnostico(),
                entity.getStatus(),
                entity.getCriadoEm(),
                entity.getEnviadoEm(),
                insumos,
                servicos
              
        );
    }
}