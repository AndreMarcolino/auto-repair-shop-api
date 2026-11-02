package br.com.autorepairshop.application.cliente.shared;

import br.com.autorepairshop.domain.cliente.Cliente;

public record ClienteOutput(
        String id,
        String nome,
        Short tipoPessoa,
        String cpfCnpj,
        String email,
        String telefone,
        boolean ativo
    ) 
	{
	
    public static ClienteOutput fromDomain(Cliente cliente) {
        return new ClienteOutput(
                cliente.getId().toString(),
                cliente.getNome(),
                cliente.getCpfCnpj().tipoPessoa().getCodigo(),
                cliente.getCpfCnpj().cpfCnpj(),
                cliente.getEmail().email(),
                cliente.getTelefone(),
                cliente.isAtivo()
        );
    }
}
	
