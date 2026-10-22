package br.com.autorepairshop.application.ordemservico.reprovar;

import org.springframework.stereotype.Service;

import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;
import jakarta.transaction.Transactional;

@Service
public class ReprovarOrdemServicoService implements ReprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final HistoricoStatusOrdemServicoRepository historicoRepository;
    private final CodificadorCodigoAcesso codificadorCodigoAcesso;

    public ReprovarOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            OrcamentoRepository orcamentoRepository,
            HistoricoStatusOrdemServicoRepository historicoRepository,
            CodificadorCodigoAcesso codificadorCodigoAcesso) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.historicoRepository = historicoRepository;
        this.codificadorCodigoAcesso = codificadorCodigoAcesso;
    }

    @Override
    @Transactional
    public void executar(ReprovarOrdemServicoInput input) {

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
                .orElseThrow(() -> new DomainException(
                        DomainError.CLIENTE_NAO_ENCONTRADO));

        var ordemServico = ordemServicoRepository
                .buscarPoNumero(input.numeroOrdemServico())
                .orElseThrow(() -> new DomainException(
                        DomainError.ORDEM_SERVICO_NAO_ENCONTRADA));

        if (!cliente.getId().equals(ordemServico.getClienteId())) {
            throw new DomainException(
                    DomainError.ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE);
        }

        var orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(ordemServico.getId())
                .orElseThrow(() -> new DomainException(
                        DomainError.ORCAMENTO_NAO_ENCONTRADO));

        var statusAnterior = ordemServico.getStatus();

        orcamento.reprovar();
        ordemServico.rejeitar();

        orcamentoRepository.salvar(orcamento);
        ordemServicoRepository.salvar(ordemServico);

        var historico = HistoricoStatusOrdemServico.registrar(
                ordemServico.getId(),
                statusAnterior,
                ordemServico.getStatus());

        historicoRepository.salvar(historico);
    }
}