package br.com.autorepairshop.domain.cliente;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;

public interface ClienteRepository {

    boolean existeCpfCnpj(CpfCnpj cpfCnpj);

    void salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);
    
    Page<Cliente> buscar(
            String nome,
            Short tipoPessoa,
            String cpfCnpj,
            String email,
            String telefone,
            Boolean ativo,
            Pageable pageable
        );
    
    Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj);
}