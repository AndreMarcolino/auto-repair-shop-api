package br.com.autorepairshop.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.HistoricoStatusOrdemServicoEntity;


public interface HistoricoStatusOrdemServicoJpaRepository
        extends JpaRepository<HistoricoStatusOrdemServicoEntity, UUID> {

    List<HistoricoStatusOrdemServicoEntity>
            findByOrdemServicoIdOrderByDataHoraAsc(UUID ordemServicoId);
}