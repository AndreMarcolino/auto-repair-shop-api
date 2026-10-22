package br.com.autorepairshop.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.autorepairshop.infrastructure.persistence.OrdemServicoEntity;

public interface OrdemServicoJpaRepository
        extends JpaRepository<OrdemServicoEntity, UUID> {
	
	List<OrdemServicoEntity> findByStatus(short status);
	
	@Query(value = "SELECT nextval('seq_numero_ordem_servico')", nativeQuery = true)
	Long proximoNumero();
	
	List<OrdemServicoEntity> findByStatusIn(
	        List<Short> status);
	
    Optional<OrdemServicoEntity> findByNumero(String numero);	
}