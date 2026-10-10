package br.com.autorepairshop.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.infrastructure.persistence.entity.ClienteEntity;
import br.com.autorepairshop.infrastructure.persistence.repository.ClienteJpaRepository;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(
            ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existeCpfCnpj(CpfCnpj cpfCnpj) {
        return jpaRepository
                .existsByCpfCnpj_TipoPessoaAndCpfCnpj_CpfCnpj(
                        cpfCnpj.tipoPessoa().getCodigo(),
                        cpfCnpj.cpfCnpj());
    }

    @Override
    public void salvar(Cliente cliente) {
        ClienteEntity entity = ClienteEntity.deDominio(cliente);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(ClienteEntity::paraDominio);
    }

	@Override
	public Page<Cliente> buscar(String nome, Short tipoPessoa, String cpfCnpj, String email, String telefone,
			Boolean ativo, Pageable pageable) {
		return jpaRepository.buscar(nome, tipoPessoa, cpfCnpj, email, telefone, ativo, pageable).
				map(ClienteEntity::paraDominio);
	}
	
	  
	  public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
		    return jpaRepository
		        .findByCpfCnpj_TipoPessoaAndCpfCnpj_CpfCnpj(
		            cpfCnpj.tipoPessoa().getCodigo(),
		            cpfCnpj.cpfCnpj()
		        ).
		        map(ClienteEntity::paraDominio);
		}

	
}


