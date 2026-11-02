package br.com.autorepairshop.domain.shared.repository;

import java.util.Optional;

public interface DomainRepository<T, ID> {
	Optional<T> buscarPorId(ID id);
	void excluir(ID id);
}
