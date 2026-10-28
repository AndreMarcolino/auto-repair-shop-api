package br.com.autorepairshop.application.cliente.criar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.Email;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;

@Service
public class CriarClienteService implements CriarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CriarClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public CriarClienteOutput executar(CriarClienteInput input) {

        var tipoPessoa = TipoPessoa.deCodigo(input.tipoPessoa());
        var cpfCnpj = CpfCnpj.criar(tipoPessoa,input.cpfCnpj());
        var email = Email.criar(input.email());
                
        if (clienteRepository.existeCpfCnpj(cpfCnpj)) {
            throw new DomainException(DomainError.CPF_CNPJ_JA_CADASTRADO);
        }

        var cliente = Cliente.criar(input.nome(),cpfCnpj,input.telefone(),email);
        
        clienteRepository.salvar(cliente);

        return  new CriarClienteOutput(cliente.getId());

     
    }
}
