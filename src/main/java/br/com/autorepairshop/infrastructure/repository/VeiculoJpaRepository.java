package br.com.autorepairshop.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.autorepairshop.infrastructure.persistence.VeiculoEntity;

public interface VeiculoJpaRepository
extends JpaRepository<VeiculoEntity, UUID> {
	
	boolean existsByPlaca_Placa(String placa);
	
	 @Query("""
		        SELECT v FROM VeiculoEntity v
		        WHERE (CAST(:placa AS string) IS NULL
		               OR v.placa.placa = CAST(:placa AS string))
		          AND (CAST(:marca AS string) IS NULL
		               OR LOWER(v.marca) LIKE
		                  LOWER(CONCAT('%', CAST(:marca AS string), '%')))
		          AND (CAST(:modelo AS string) IS NULL
		               OR LOWER(v.modelo) LIKE
		                  LOWER(CONCAT('%', CAST(:modelo AS string), '%')))
		          AND (:anoFabricacao IS NULL
		               OR v.anoFabricacao = :anoFabricacao)
		        """)
		    Page<VeiculoEntity> buscar(
		        @Param("placa") String placa,
		        @Param("marca") String marca,
		        @Param("modelo") String modelo,
		        @Param("anoFabricacao") Integer anoFabricacao,
		        Pageable paginacao
		    );
}
