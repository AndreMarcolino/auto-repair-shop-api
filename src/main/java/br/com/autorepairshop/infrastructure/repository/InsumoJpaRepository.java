package br.com.autorepairshop.infrastructure.repository;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.autorepairshop.infrastructure.persistence.InsumoEntity;

public interface InsumoJpaRepository extends JpaRepository<InsumoEntity, UUID> {
	
	@Query("""
	        SELECT i FROM InsumoEntity i
	        WHERE (
	            CAST(:descricao AS string) IS NULL
	            OR LOWER(i.descricao) LIKE
	               LOWER(CONCAT('%', CAST(:descricao AS string), '%'))
	        )
	        AND (
	            :valorReferencia IS NULL
	            OR i.valorReferencia = :valorReferencia
	        )
	        AND (
	            :quantidadeEstoque IS NULL
	            OR i.quantidadeEstoque = :quantidadeEstoque
	        )
	        """)
	Page<InsumoEntity> buscar(
	        @Param("descricao") String descricao,
	        @Param("valorReferencia") BigDecimal valorReferencia,
	        @Param("quantidadeEstoque") Integer quantidadeEstoque, 
	        Pageable paginacao);
	
	 List<InsumoEntity> findAllByIdIn(Collection<UUID> ids);;
}
