package br.com.autorepairshop.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.autorepairshop.infrastructure.persistence.EstoqueEntity;
import jakarta.persistence.LockModeType;

public interface EstoqueJpaRepository
        extends JpaRepository<EstoqueEntity, UUID> {

    Optional<EstoqueEntity> findByInsumoId(UUID insumoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e
        FROM EstoqueEntity e
        WHERE e.insumoId = :insumoId
    """)
    Optional<EstoqueEntity> buscarParaAtualizacao(
            @Param("insumoId") UUID insumoId
    );
}