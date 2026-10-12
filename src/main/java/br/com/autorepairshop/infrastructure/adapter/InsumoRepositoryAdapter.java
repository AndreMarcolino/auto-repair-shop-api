package br.com.autorepairshop.infrastructure.adapter;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.autorepairshop.domain.insumo.Insumo;
import br.com.autorepairshop.domain.insumo.InsumoRepository;
import br.com.autorepairshop.domain.shared.Pagina;
import br.com.autorepairshop.domain.shared.Paginacao;
import br.com.autorepairshop.infrastructure.persistence.InsumoEntity;
import br.com.autorepairshop.infrastructure.repository.InsumoJpaRepository;

@Repository
class InsumoRepositoryAdapter implements InsumoRepository {

    private final InsumoJpaRepository jpaRepository;

    public InsumoRepositoryAdapter(InsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Insumo salvar(Insumo insumo) {
        InsumoEntity entity = InsumoEntity.fromDomain(insumo);
        InsumoEntity salva = jpaRepository.save(entity);
        return salva.toDomain();
    }
    
    @Override
    public Optional<Insumo> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(InsumoEntity::toDomain);
    }
    
    @Override
    public Pagina<Insumo> buscar(
            String descricao,
            BigDecimal valorReferencia,
            Integer quantidadeEstoque,
            Paginacao paginacao) {

        Pageable pageable = PageRequest.of(
                paginacao.pagina(),
                paginacao.tamanho());

        Page<InsumoEntity> page = jpaRepository.buscar(
                descricao,
                valorReferencia,
                quantidadeEstoque,
                pageable);

        var insumos = page.getContent()
                .stream()
                .map(InsumoEntity::toDomain)
                .toList();

        return new Pagina<Insumo>(
                insumos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
    
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }
}
