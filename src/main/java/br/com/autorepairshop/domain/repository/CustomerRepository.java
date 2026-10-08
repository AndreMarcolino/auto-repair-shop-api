package br.com.autorepairshop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autorepairshop.domain.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);
}