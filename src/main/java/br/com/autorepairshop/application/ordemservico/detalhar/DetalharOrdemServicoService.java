package br.com.autorepairshop.application.ordemservico.detalhar;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput.ClienteOutput;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput.VeiculoOutput;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput.OrcamentoOutput;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput.ItemServicoOutput;
import br.com.autorepairshop.application.ordemservico.detalhar.DetalharOrdemServicoOutput.ItemInsumoOutput;

import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.cliente.Cliente;
import br.com.autorepairshop.domain.veiculo.Veiculo;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.veiculo.VeiculoRepository;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;

@Service
public class DetalharOrdemServicoService implements DetalharOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrcamentoRepository orcamentoRepository;

    public DetalharOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            OrcamentoRepository orcamentoRepository) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    @Transactional(readOnly = true)
    public DetalharOrdemServicoOutput executar(String id) {

    	UUID ordemServicoId = null;
    	try {
    		ordemServicoId = UUID.fromString(id);
    	}catch(Exception ex) {
    		 throw new DomainException(DomainError.ID_ORDEM_SERVICO_INVALIDO);
    	}
        Optional<OrdemServico> ordemServico = ordemServicoRepository
                .buscarPorId(ordemServicoId);
        
        if(ordemServico.isEmpty()) {
        	return null;
        }

        Cliente cliente = clienteRepository
                .buscarPorId(ordemServico.get().getClienteId())
                .orElseThrow(() -> new IllegalStateException(
                        "Veículo da ordem de serviço não encontrado."));
                
        Veiculo veiculo = veiculoRepository
                .buscarPorId(ordemServico.get().getVeiculoId())
                .orElseThrow(() -> new IllegalStateException(
                        "Veículo da ordem de serviço não encontrado."));

        Orcamento orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(ordemServicoId)
                .orElse(null);

        return new DetalharOrdemServicoOutput(
                ordemServico.get().getId(),
                ordemServico.get().getNumero(),
                ordemServico.get().getStatus(),
                ordemServico.get().getProblemaRelatado(),
                ordemServico.get().getDataAbertura(),
                ordemServico.get().getDataInicioServico(),
                ordemServico.get().getDataFimServico(),
                ordemServico.get().getDataEntrega(),
                paraClienteOutput(cliente),
                paraVeiculoOutput(veiculo),
                paraOrcamentoOutput(orcamento)
        );
    }

    private ClienteOutput paraClienteOutput(Cliente cliente) {
        return new ClienteOutput(
                cliente.getId(),
                cliente.getNome()
        );
    }

    private VeiculoOutput paraVeiculoOutput(Veiculo veiculo) {
        return new VeiculoOutput(veiculo.getId(), veiculo.getPlaca().placa(), veiculo.getMarca(), veiculo.getModelo());
    }

    private OrcamentoOutput paraOrcamentoOutput(Orcamento orcamento) {
        if (orcamento == null) {
            return null;
        }

        var servicos = orcamento.getServicos().stream()
                .map(item -> {
                    BigDecimal total = item.getQuantidade()
                            .multiply(item.getValorUnitario());

                    return new ItemServicoOutput(
                            item.getDescricao(),
                            item.getQuantidade(),
                            item.getValorUnitario(),
                            total
                    );
                })
                .toList();

        var insumos = orcamento.getInsumos().stream()
                .map(item -> {
                    BigDecimal total = item.getQuantidade()
                            .multiply(item.getValorUnitario());

                    return new ItemInsumoOutput(
                            item.getDescricao(),
                            item.getQuantidade(),
                            item.getValorUnitario(),
                            total
                    );
                })
                .toList();

        BigDecimal valorServicos = servicos.stream()
                .map(ItemServicoOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorInsumos = insumos.stream()
                .map(ItemInsumoOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotal = valorServicos.add(valorInsumos);

        return new OrcamentoOutput(
                orcamento.getDiagnostico(),
                valorTotal,
                servicos,
                insumos
        );
    }
}