package br.com.autorepairshop.infrastructure.repository;


import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.autorepairshop.infrastructure.persistence.ServicoEntity;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {
	
	@Query("""
		    SELECT s FROM ServicoEntity s
		    WHERE (CAST(:descricao AS string) IS NULL
		           OR LOWER(s.descricao) LIKE
		              LOWER(CONCAT('%', CAST(:descricao AS string), '%')))
		      AND (:valorReferencia IS NULL
		           OR s.valorReferencia = :valorReferencia)
		    """)
		Page<ServicoEntity> buscar(
		    @Param("descricao") String descricao,
		    @Param("valorReferencia") BigDecimal valorReferencia,
		    Pageable paginacao
		);
}
