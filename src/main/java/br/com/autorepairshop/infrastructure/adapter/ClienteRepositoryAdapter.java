package br.com.autorepairshop.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.infrastructure.persistence.ClienteEntity;
import br.com.autorepairshop.infrastructure.repository.ClienteJpaRepository;

@Repository
class ClienteRepositoryAdapter implements ClienteRepository {

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
        ClienteEntity entity = ClienteEntity.toEntity(cliente);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(ClienteEntity::toDomain);
    }

	@Override
	public Pagina<Cliente> buscar(String nome, Short tipoPessoa, String cpfCnpj, String email, String telefone,
			Boolean ativo, Paginacao paginacao) {
		
		Pageable pageable = PageRequest.of(paginacao.pagina(),paginacao.tamanho());
		Page<ClienteEntity> page = jpaRepository.buscar(nome, tipoPessoa, cpfCnpj, email, telefone, ativo, pageable);
		
		List<Cliente> clientes = page.getContent()
		            .stream()
		            .map(ClienteEntity::toDomain)
		            .toList();

		  
		return new Pagina<Cliente>(
				clientes,
		            page.getNumber(),
		            page.getSize(),
		            page.getTotalElements(),
		            page.getTotalPages()
		        );
	}
	
	  
	  public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
		    return jpaRepository
		        .findByCpfCnpj_TipoPessoaAndCpfCnpj_CpfCnpj(
		            cpfCnpj.tipoPessoa().getCodigo(),
		            cpfCnpj.cpfCnpj()
		        ).
		        map(ClienteEntity::toDomain);
		}

	
}


