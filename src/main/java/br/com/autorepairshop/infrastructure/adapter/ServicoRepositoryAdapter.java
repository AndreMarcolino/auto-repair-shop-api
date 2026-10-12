package br.com.autorepairshop.infrastructure.adapter;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.infrastructure.persistence.ServicoEntity;
import br.com.autorepairshop.infrastructure.repository.ServicoJpaRepository;

@Repository
public class ServicoRepositoryAdapter implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;

    public ServicoRepositoryAdapter(ServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity entity = ServicoEntity.toEntity(servico);
        ServicoEntity salvo = jpaRepository.save(entity);
        return salvo.toDomain();
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(ServicoEntity::toDomain);
    }
    
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

	@Override
	public Pagina<Servico> buscar(String descricao, BigDecimal valorReferencia, Paginacao paginacao) {
		

		Pageable pageable = PageRequest.of(paginacao.pagina(),paginacao.tamanho());
		Page<ServicoEntity> page =  jpaRepository.buscar(
	               descricao,
	               valorReferencia,
	               pageable
	            );
		
		var servicos = page.getContent()
	            .stream()
	            .map(ServicoEntity::toDomain)
	            .toList();
		
		return new Pagina<Servico>(
				servicos,
		            page.getNumber(),
		            page.getSize(),
		            page.getTotalElements(),
		            page.getTotalPages()
		        );
	}
    
    
}
