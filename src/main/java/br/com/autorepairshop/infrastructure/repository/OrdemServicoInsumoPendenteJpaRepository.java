package br.com.autorepairshop.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.OrdemServicoInsumoPendenteEntity;

public interface OrdemServicoInsumoPendenteJpaRepository
        extends JpaRepository<OrdemServicoInsumoPendenteEntity, UUID> {

    List<OrdemServicoInsumoPendenteEntity> findByOrdemServicoId(
            UUID ordemServicoId);

    void deleteByOrdemServicoId(UUID ordemServicoId);
}