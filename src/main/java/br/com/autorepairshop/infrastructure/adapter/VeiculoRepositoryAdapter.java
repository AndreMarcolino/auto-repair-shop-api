package br.com.autorepairshop.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.infrastructure.persistence.VeiculoEntity;
import br.com.autorepairshop.infrastructure.repository.VeiculoJpaRepository;

@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepository {

	private final VeiculoJpaRepository jpaRepository;

	public VeiculoRepositoryAdapter(VeiculoJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public Veiculo salvar(Veiculo veiculo) {
		VeiculoEntity entidade = VeiculoEntity.toEntity(veiculo);

		VeiculoEntity salva = jpaRepository.save(entidade);

		return salva.toDomain();
	}

	public boolean existePlaca(Placa placa) {
		return jpaRepository.existsByPlaca_Placa(placa.placa());
	}

	@Override
	public Optional<Veiculo> buscarPorId(UUID id) {
		return jpaRepository.findById(id).map(VeiculoEntity::toDomain);
	}

	@Override
	public Pagina<Veiculo> buscar(String placa, String marca, String modelo, Integer anoFabricacao, Paginacao paginacao) {

		Pageable pageable = PageRequest.of(paginacao.pagina(),paginacao.tamanho());
		Page<VeiculoEntity> page = jpaRepository.buscar(placa, marca, modelo, anoFabricacao, pageable);
		
		
		var veiculos = page.getContent()
	            .stream()
	            .map(VeiculoEntity::toDomain)
	            .toList();
	 return new Pagina<Veiculo>(
			 veiculos,
	            page.getNumber(),
	            page.getSize(),
	            page.getTotalElements(),
	            page.getTotalPages()
	        ); 
				
	}

	@Override
	public void excluir(UUID id) {
		jpaRepository.deleteById(id);
	}

}
