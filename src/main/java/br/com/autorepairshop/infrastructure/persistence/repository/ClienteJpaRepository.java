package br.com.autorepairshop.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.autorepairshop.infrastructure.persistence.entity.ClienteEntity;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

	@Query("""
		    SELECT c FROM ClienteEntity c
		    WHERE (CAST(:nome AS string) IS NULL
		           OR LOWER(c.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%')))
		      AND (:tipoPessoa IS NULL OR c.cpfCnpj.tipoPessoa = :tipoPessoa)
		      AND (CAST(:cpfCnpj AS string) IS NULL
		           OR c.cpfCnpj.cpfCnpj = CAST(:cpfCnpj AS string))
		      AND (CAST(:email AS string) IS NULL
		           OR LOWER(c.email.email) = LOWER(CAST(:email AS string)))
		      AND (CAST(:telefone AS string) IS NULL
		           OR c.telefone = CAST(:telefone AS string))
		      AND (:ativo IS NULL OR c.ativo = :ativo)
		    """)
    Page<ClienteEntity> buscar(
        @Param("nome") String nome,
        @Param("tipoPessoa") Short tipoPessoa,
        @Param("cpfCnpj") String cpfCnpj,
        @Param("email") String email,
        @Param("telefone") String telefone,
        @Param("ativo") Boolean ativo,
        Pageable pageable
    );

    boolean existsByCpfCnpj_TipoPessoaAndCpfCnpj_CpfCnpj(
        Short tipoPessoa,
        String cpfCnpj
    );
    
    Optional<ClienteEntity>
    findByCpfCnpj_TipoPessoaAndCpfCnpj_CpfCnpj(
        Short tipoPessoa,
        String cpfCnpj
    );
}
