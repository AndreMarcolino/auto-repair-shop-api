package br.com.autorepairshop.infrastructure.persistence.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.infrastructure.persistence.entity.ServicoEntity;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {
}
