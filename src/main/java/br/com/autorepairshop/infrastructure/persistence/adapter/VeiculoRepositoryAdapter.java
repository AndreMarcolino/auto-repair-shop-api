package br.com.autorepairshop.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.shared.valueobject.Placa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.infrastructure.persistence.entity.VeiculoEntity;
import br.com.autorepairshop.infrastructure.persistence.repository.VeiculoJpaRepository;

@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepository {

    private final VeiculoJpaRepository jpaRepository;

    public VeiculoRepositoryAdapter(VeiculoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoEntity entidade = VeiculoEntity.fromDomain(veiculo);

        VeiculoEntity salva = jpaRepository.save(entidade);

        return salva.toDomain();
    }
    
    public boolean existePlaca(Placa placa) {
        return jpaRepository.existsByPlaca_Placa(placa.placa());
    }
    
    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(VeiculoEntity::toDomain);
    }
    
    @Override
    public Page<Veiculo> buscar(
            String placa,
            String marca,
            String modelo,
            Integer anoFabricacao,
            Pageable pageable) {

        return jpaRepository.buscar(
                placa,
                marca,
                modelo,
                anoFabricacao,
                pageable
            )
            .map(VeiculoEntity::toDomain);
    }
    
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

}
