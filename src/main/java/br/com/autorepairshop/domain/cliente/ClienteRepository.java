package br.com.autorepairshop.domain.cliente;

import java.util.Optional;
import java.util.UUID;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;

public interface ClienteRepository {

    boolean existeCpfCnpj(CpfCnpj cpfCnpj);

    void salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);
    
    Pagina<Cliente> buscar(
            String nome,
            Short tipoPessoa,
            String cpfCnpj,
            String email,
            String telefone,
            Boolean ativo,
            Paginacao paginacao
        );
    
    Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj);
}