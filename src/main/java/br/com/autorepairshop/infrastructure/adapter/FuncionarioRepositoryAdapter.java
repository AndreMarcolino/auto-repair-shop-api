package br.com.autorepairshop.infrastructure.adapter;


import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.funcionario.Funcionario;
import br.com.autorepairshop.domain.funcionario.FuncionarioRepository;
import br.com.autorepairshop.infrastructure.persistence.FuncionarioEntity;
import br.com.autorepairshop.infrastructure.repository.FuncionarioJpaRepository;

@Repository
public class FuncionarioRepositoryAdapter
        implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioRepositoryAdapter(
            FuncionarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Funcionario> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(this::paraDominio);
    }

    @Override
    public Optional<Funcionario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::paraDominio);
    }

    private Funcionario paraDominio(FuncionarioEntity entity) {
        return Funcionario.recria(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getSenhaHash(),
                entity.getTipo(),
                entity.getCriadoEm(),
                entity.isAtivo()
        );
    }
}