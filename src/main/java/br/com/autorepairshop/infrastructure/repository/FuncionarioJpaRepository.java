package br.com.autorepairshop.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.FuncionarioEntity;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, UUID> {

	Optional<FuncionarioEntity> findByEmail(String email);
}