package br.com.autorepairshop.infrastructure.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.infrastructure.persistence.HistoricoStatusOrdemServicoEntity;
import br.com.autorepairshop.infrastructure.repository.HistoricoStatusOrdemServicoJpaRepository;

@Repository
public class HistoricoStatusOrdemServicoRepositoryAdapter
        implements HistoricoStatusOrdemServicoRepository {

    private final HistoricoStatusOrdemServicoJpaRepository jpaRepository;

    public HistoricoStatusOrdemServicoRepositoryAdapter(
            HistoricoStatusOrdemServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(HistoricoStatusOrdemServico historico) {
        jpaRepository.save(
                HistoricoStatusOrdemServicoEntity.toEntity(historico));
    }

    @Override
    public List<HistoricoStatusOrdemServico> buscarPorOrdemServicoId(
            UUID ordemServicoId) {

        return jpaRepository
                .findByOrdemServicoIdOrderByDataHoraAsc(ordemServicoId)
                .stream()
                .map(HistoricoStatusOrdemServicoEntity::toDomain)
                .toList();
    }
}