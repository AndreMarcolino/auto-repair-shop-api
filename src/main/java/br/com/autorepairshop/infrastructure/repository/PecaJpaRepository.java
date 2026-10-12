package br.com.autorepairshop.infrastructure.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.PecaEntity;

public interface PecaJpaRepository extends JpaRepository<PecaEntity, UUID> {
}
