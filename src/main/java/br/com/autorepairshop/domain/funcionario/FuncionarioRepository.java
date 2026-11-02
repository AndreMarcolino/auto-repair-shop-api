package br.com.autorepairshop.domain.funcionario;

import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository {

    Optional<Funcionario> buscarPorId(UUID id);

    Optional<Funcionario> buscarPorEmail(String email);
}