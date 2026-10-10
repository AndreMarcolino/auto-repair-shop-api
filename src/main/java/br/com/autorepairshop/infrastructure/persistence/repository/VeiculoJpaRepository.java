package br.com.autorepairshop.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.entity.VeiculoEntity;

public interface VeiculoJpaRepository
extends JpaRepository<VeiculoEntity, UUID> {
	
	boolean existsByPlaca_Placa(String placa);
	
	
}
