package br.com.autorepairshop.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.OrcamentoEntity;

public interface OrcamentoJpaRepository
        extends JpaRepository<OrcamentoEntity, UUID> {

    Optional<OrcamentoEntity> findByOrdemServicoId(
            UUID ordemServicoId);
}
