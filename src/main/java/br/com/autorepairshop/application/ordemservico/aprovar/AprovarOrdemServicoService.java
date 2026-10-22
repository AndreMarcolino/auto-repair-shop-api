
package br.com.autorepairshop.application.ordemservico.aprovar;

import java.util.ArrayList;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.cliente.ClienteRepository;
import br.com.autorepairshop.domain.estoque.Estoque;
import br.com.autorepairshop.domain.estoque.EstoqueRepository;
import br.com.autorepairshop.domain.orcamento.Orcamento;
import br.com.autorepairshop.domain.orcamento.OrcamentoRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServico;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendente;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoInsumoPendenteRepository;
import br.com.autorepairshop.domain.ordemservico.OrdemServicoRepository;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServico;
import br.com.autorepairshop.domain.ordemservico.historico.HistoricoStatusOrdemServicoRepository;
import br.com.autorepairshop.domain.shared.DomainError;
import br.com.autorepairshop.domain.shared.DomainException;
import br.com.autorepairshop.domain.shared.valueobject.CpfCnpj;
import br.com.autorepairshop.domain.shared.valueobject.TipoPessoa;
import br.com.autorepairshop.infrastructure.security.CodificadorCodigoAcesso;

@Service
public class AprovarOrdemServicoService
        implements AprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final EstoqueRepository estoqueRepository;
    private final HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository;
    private final OrdemServicoInsumoPendenteRepository ordemServicoInsumoPendenteRepository;
    private final CodificadorCodigoAcesso codificadorCodigoAcesso;
    

    public AprovarOrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            OrcamentoRepository orcamentoRepository,
            EstoqueRepository estoqueRepository,
            HistoricoStatusOrdemServicoRepository historicoStatusOrdemServicoRepository,
            OrdemServicoInsumoPendenteRepository ordemServicoInsumoPendenteRepository,
            CodificadorCodigoAcesso codificadorCodigoAcesso) {

        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.estoqueRepository = estoqueRepository;
        this.historicoStatusOrdemServicoRepository =historicoStatusOrdemServicoRepository;
        this.ordemServicoInsumoPendenteRepository = ordemServicoInsumoPendenteRepository;
        this.codificadorCodigoAcesso = codificadorCodigoAcesso;
    }

    @Override
    @Transactional
    public void executar(AprovarOrdemServicoInput input) {

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
                .orElseThrow(() ->
                        new DomainException(
                                DomainError.CLIENTE_NAO_ENCONTRADO));

        OrdemServico ordemServico = ordemServicoRepository.buscarPoNumero(input.numeroOrdemServico())
                .orElseThrow(() ->
                        new DomainException(
                                DomainError.ORDEM_SERVICO_NAO_ENCONTRADA));

        
        if (!cliente.getId().equals(ordemServico.getClienteId())) {
            throw new DomainException(
                    DomainError.ORDEM_SERVICO_NAO_PERTECEN_AO_CLIENTE);
        }
        
        var codigoAcessoHash = codificadorCodigoAcesso.codificar(input.codigoVerificacao());
        if(!codigoAcessoHash.equals(ordemServico.getCodigoAcessoHash())) {
        	 throw new DomainException(
                     DomainError.CODIGO_ACESSO_INVALIDO);
        }

        Orcamento orcamento = orcamentoRepository
                .buscarPorOrdemServicoId(ordemServico.getId())
                .orElseThrow(() ->
                        new DomainException(
                                DomainError.ORCAMENTO_NAO_ENCONTRADO));

        var statusAnterior = ordemServico.getStatus();
        orcamento.aprovar();
        
        boolean estoqueDisponivel = baixarInsumosSeDisponiveis(orcamento);

        if (estoqueDisponivel) {
            ordemServico.aprovar();
        } else {
            ordemServico.aguardarPecas();
            registrarInsumosPendentes(ordemServico, orcamento);
        }

        orcamentoRepository.salvar(orcamento);
        ordemServicoRepository.salvar(ordemServico);

        var historico = HistoricoStatusOrdemServico.registrar(
                ordemServico.getId(),
                statusAnterior,
                ordemServico.getStatus()
        );

        historicoStatusOrdemServicoRepository.salvar(historico);

     
    }
    
    private void registrarInsumosPendentes(OrdemServico ordemServico, Orcamento orcamento) {
        var pendencias = orcamento.getInsumos()
                .stream()
                .map(item -> OrdemServicoInsumoPendente.criar(
                        ordemServico.getId(),
                        item.getInsumoId(),
                        item.getQuantidade()
                ))
                .toList();

        pendencias.forEach(ordemServicoInsumoPendenteRepository::salvar);
    }

   
    private boolean baixarInsumosSeDisponiveis(Orcamento orcamento) {

        var itens = orcamento.getInsumos();

        var itensOrdenados = itens.stream()
                .sorted(Comparator.comparing(
                        item -> item.getInsumoId().toString()))
                .toList();

        var estoques = new ArrayList<Estoque>();

        for (var item : itensOrdenados) {

            Estoque estoque = estoqueRepository.buscarParaAtualizacao(item.getInsumoId()).orElse(null);

            boolean possuiQuantidade = estoque.possuiDisponibilidade(item.getQuantidade());
            if (estoque == null || !possuiQuantidade) {
                return false;
            }
            estoques.add(estoque);
        }

        for (int i = 0; i < itensOrdenados.size(); i++) {
            var item = itensOrdenados.get(i);
            var estoque = estoques.get(i);
            estoque.baixar(item.getQuantidade());
            estoqueRepository.salvar(estoque);
        }

        return true;
    }
}
