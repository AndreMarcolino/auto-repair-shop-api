package br.com.autorepairshop.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.OrdemServicoEntity;

public interface OrdemServicoJpaRepository
        extends JpaRepository<OrdemServicoEntity, UUID> {
}