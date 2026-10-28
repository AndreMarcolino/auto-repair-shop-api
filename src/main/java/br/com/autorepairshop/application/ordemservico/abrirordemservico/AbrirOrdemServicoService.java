package br.com.autorepairshop.application.ordemservico.abrirordemservico;

import java.time.Year;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;
import br.com.autorepairshop.infrastructure.security.GeradorCodigoAcesso;

@Service
public class AbrirOrdemServicoService implements AbrirOrdemServicoUseCase{

	private final ClienteRepository clienteRepository;
	
	private final VeiculoRepository veiculoRepository;
	
	private final OrdemServicoRepository ordemServicoRepository;
	
	private final GeradorCodigoAcesso geradorCodigoAcesso;
	private final CodificadorCodigoAcesso codificadorCodigoAcesso;
	
	public AbrirOrdemServicoService(ClienteRepository clienteRepository,
			VeiculoRepository veiculoRepository,
			OrdemServicoRepository ordemServicoRepository,
			GeradorCodigoAcesso geradorCodigoAcesso,
			CodificadorCodigoAcesso codificadorCodigoAcesso) {
		this.clienteRepository = clienteRepository;
		this.veiculoRepository = veiculoRepository;
		this.ordemServicoRepository = ordemServicoRepository;
		this.geradorCodigoAcesso = geradorCodigoAcesso;
		this.codificadorCodigoAcesso = codificadorCodigoAcesso;
	}
	@Override
	@Transactional
	public AbrirOrdemServicoOutput executar(AbrirOrdemServicoInput input) {
		
		UUID idCliente;
        UUID idVeiculo;
        
        if(input.idCliente() == null) {
        	throw new DomainException(DomainError.ID_CLIENTE_INVALIDO);
        }
        
        if(input.idVeiculo() == null) {
        	throw new DomainException(DomainError.ID_VEICULO_INVALIDO);
        }
        
        try {
        	
            idCliente = UUID.fromString(input.idCliente());
        } catch (IllegalArgumentException | NullPointerException exception) {
        	throw new DomainException(DomainError.ID_CLIENTE_INVALIDO);
        }
        
        try {
        	idVeiculo = UUID.fromString(input.idVeiculo());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException(DomainError.ID_VEICULO_INVALIDO);
        }
        
		Cliente cliente = clienteRepository.buscarPorId(idCliente)
                .orElseThrow(() ->  new DomainException(DomainError.CLIENTE_NAO_ENCONTRADO));
		
		if(!cliente.isAtivo()) {
			throw new DomainException(DomainError.CLIENTE_DESATIVADO_ABERTURA_OS);
		}
		
		veiculoRepository.buscarPorId(idVeiculo).
				 orElseThrow(() ->  
				 new DomainException(DomainError.VEICULO_NAO_ENCONTRADO));

		long sequencial = ordemServicoRepository.proximoNumero();

		String numero = "OS-%d-%06d".formatted(Year.now().getValue(),sequencial);
		String codigoAcesso = geradorCodigoAcesso.gerar();
		String codigoAcessoHash = codificadorCodigoAcesso.codificar(codigoAcesso);

		var ordemServico = OrdemServico.abrir(numero, codigoAcessoHash,idCliente, idVeiculo, input.problemaRelatado());
		
		this.ordemServicoRepository.salvar(ordemServico);
		
		return new AbrirOrdemServicoOutput(numero, codigoAcesso);

	}
}
