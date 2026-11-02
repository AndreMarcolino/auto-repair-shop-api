package br.com.autorepairshop.application.ordemservico.detalharcliente;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteOutput.ClienteOutput;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteOutput.ItemInsumoOutput;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteOutput.ItemServicoOutput;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteOutput.OrcamentoOutput;
import br.com.autorepairshop.application.ordemservico.detalharcliente.DetalharOrdemServicoClienteOutput.VeiculoOutput;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;

@Service
public class DetalharOrdemServicoClienteService
        implements DetalharOrdemServicoClienteUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final CodificadorCodigoAcesso codificadorCodigoAcesso;

    public DetalharOrdemServicoClienteService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            OrcamentoRepository orcamentoRepository,
            CodificadorCodigoAcesso codificadorCodigoAcesso) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.codificadorCodigoAcesso = codificadorCodigoAcesso;
    }

    @Override
    @Transactional(readOnly = true)
    public DetalharOrdemServicoClienteOutput executar(
    		DetalharOrdemServicoClienteInput input) {

    	if(input.cpfCnpj() == null || input.cpfCnpj().isBlank()) {
    		throw new DomainException(DomainError.CNPJ_CNPJ_OBRIGATORIO);
    	}
    	
    	if(input.numeroOrdemServico() == null || input.cpfCnpj().isBlank()) {
    		throw new DomainException(DomainError.ORDEM_SERVICO_OBRIGATORIO);
    	}
    	
    	if(input.codigoVerificacao() == null || input.codigoVerificacao().isBlank()) {
    		throw new DomainException(DomainError.CODIGO_VERIFICACAO_OBRIGATORIO);
    	}
    	
    	String documento = input.cpfCnpj().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

    	CpfCnpj cpfCnpj = null;
    	if (documento.length() == 11 && documento.matches("\\d{11}")) {
    	    cpfCnpj = new CpfCnpj(TipoPessoa.PESSOA_FISICA, documento);
    	} else if (documento.length() == 14) {
    		cpfCnpj = new CpfCnpj(TipoPessoa.PESSOA_FISICA, documento);
    	} else {
    		throw new DomainException(DomainError.CPF_CNPJ_INVALIDO);
    	}
        
      
        var cliente = clienteRepository
                .buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() ->new DomainException(DomainError.CLIENTE_NAO_ENCONTRADO));

        OrdemServico ordemServico = ordemServicoRepository.buscarPoNumero(input.numeroOrdemServico()).orElse(null);
        if(ordemServico == null) {
        	return null;
        }

        
        if (!cliente.getId().equals(ordemServico.getClienteId())) {
            throw new DomainException(DomainError.ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE);
        }

        var codigoAcessoHash = codificadorCodigoAcesso.codificar(input.codigoVerificacao());
        if(!codigoAcessoHash.equals(ordemServico.getCodigoAcessoHash())) {
        	 throw new DomainException(
                     DomainError.CODIGO_ACESSO_INVALIDO);
        }
      
       
        Veiculo veiculo = veiculoRepository
                .buscarPorId(ordemServico.getVeiculoId())
                .orElseThrow(() ->new DomainException(DomainError.VEICULO_NAO_ENCONTRADO));

        Orcamento orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(ordemServico.getId())
                .orElse(null);

        return new DetalharOrdemServicoClienteOutput(
                ordemServico.getNumero(),
                ordemServico.getStatus(),
                ordemServico.getProblemaRelatado(),
                ordemServico.getDataAbertura(),
                ordemServico.getDataInicioServico(),
                ordemServico.getDataFimServico(),
                ordemServico.getDataEntrega(),
                paraClienteOutput(cliente),
                paraVeiculoOutput(veiculo),
                paraOrcamentoOutput(orcamento)
        );
    }

    
    private ClienteOutput paraClienteOutput(Cliente cliente) {
        return new ClienteOutput(cliente.getNome());
    }

    private VeiculoOutput paraVeiculoOutput(Veiculo veiculo) {
        return new VeiculoOutput(
                veiculo.getPlaca().placa(),
                veiculo.getMarca(),
                veiculo.getModelo()
        );
    }

    private OrcamentoOutput paraOrcamentoOutput(Orcamento orcamento) {
        if (orcamento == null) {
            return null;
        }

        var servicos = orcamento.getServicos().stream()
                .map(item -> new ItemServicoOutput(
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getQuantidade()
                                .multiply(item.getValorUnitario())
                ))
                .toList();

        var insumos = orcamento.getInsumos().stream()
                .map(item -> new ItemInsumoOutput(
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getQuantidade()
                                .multiply(item.getValorUnitario())
                ))
                .toList();

        BigDecimal valorServicos = servicos.stream()
                .map(ItemServicoOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorInsumos = insumos.stream()
                .map(ItemInsumoOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrcamentoOutput(
                orcamento.getDiagnostico(),
                valorServicos.add(valorInsumos),
                servicos,
                insumos
        );
    }
}