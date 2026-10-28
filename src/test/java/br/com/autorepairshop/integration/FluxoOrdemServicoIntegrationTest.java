package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.GeradorDados;
import br.com.autorepairshop.integration.support.Resposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Fluxos principais da oficina, de ponta a ponta:
 * abertura → diagnóstico → orçamento → aprovação do cliente → execução → pagamento → entrega.
 */
@DisplayName("Fluxos principais da Ordem de Serviço")
class FluxoOrdemServicoIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Fluxo completo feliz: RECEBIDA até ENTREGUE, com orçamento de R$ 300,00")
    void fluxoCompleto() {
        String cpf = GeradorDados.cpfValido();
        String clienteId = criarCliente(1, cpf);
        String veiculoId = criarVeiculo();

        // 1. Atendente abre a OS
        OrdemAberta ordem = abrirOrdemServico(clienteId, veiculoId);
        assertThat(ordem.numero()).isNotBlank();
        assertThat(ordem.codigoAcesso()).isNotBlank();
        assertThat(statusDa(ordem.id())).isEqualTo("RECEBIDA");

        // 2. Mecânico diagnostica e monta o orçamento (1 serviço de 100 + 4 insumos de 50 = 300)
        prepararOrcamentoAguardandoAprovacao(ordem, 10);
        Resposta aguardando = detalhar(ordem.id());
        assertThat(aguardando.texto("$.orcamento.diagnostico")).isEqualTo("Troca de peças e revisão geral");
        assertThat(aguardando.numero("$.orcamento.valorTotal")).isEqualTo(300.0);

        // 3. Cliente consulta a OS pelo link público e aprova
        Resposta consulta = api.get("/api/ordens-servico/cliente/detalhamento?numeroOrdemServico=" + ordem.numero()
                + "&codigoVerificacao=" + ordem.codigoAcesso() + "&cpfCnpj=" + cpf, null);
        assertThat(consulta.status()).as(consulta.toString()).isEqualTo(200);

        assertSucesso(aprovar(ordem, cpf), "Aprovar orçamento");
        assertThat(statusDa(ordem.id())).isEqualTo("APROVADA");

        // 4. Execução
        assertSucesso(iniciarServico(ordem.id()), "Iniciar serviço");
        assertThat(statusDa(ordem.id())).isEqualTo("EM_EXECUCAO");

        assertSucesso(finalizarServico(ordem.id()), "Finalizar serviço");
        // FINALIZADA → AGUARDANDO_PAGAMENTO acontece por evento de domínio após o commit
        aguardarStatus(ordem.id(), "AGUARDANDO_PAGAMENTO");

        // 5. Pagamento e entrega
        assertSucesso(registrarPagamento(ordem.id()), "Registrar pagamento");
        assertThat(statusDa(ordem.id())).isEqualTo("PAGA");

        assertSucesso(entregarVeiculo(ordem.id()), "Entregar veículo");
        assertThat(statusDa(ordem.id())).isEqualTo("ENTREGUE");

        // 6. OS entregue entra no cálculo do tempo médio de execução
        Resposta tempoMedio = api.get("/api/ordens-servico/tempo-medio-execucao", tokenAtendente());
        assertThat(tempoMedio.status()).as(tempoMedio.toString()).isEqualTo(200);
        assertThat(tempoMedio.numero("$.quantidadeOrdensServico")).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Cliente reprova o orçamento: OS vai para REJEITADA")
    void reprovacaoDoOrcamento() {
        String cpf = GeradorDados.cpfValido();
        OrdemAberta ordem = abrirOrdemServico(criarCliente(1, cpf), criarVeiculo());
        prepararOrcamentoAguardandoAprovacao(ordem, 10);

        assertSucesso(reprovar(ordem, cpf), "Reprovar orçamento");

        assertThat(statusDa(ordem.id())).isEqualTo("REJEITADA");
    }

    @Test
    @DisplayName("Sem estoque: aprovação leva a AGUARDANDO_PECAS e a entrada de estoque libera a OS")
    void aprovacaoSemEstoqueAguardaPecasELiberaComEntrada() {
        String cpf = GeradorDados.cpfValido();
        OrdemAberta ordem = abrirOrdemServico(criarCliente(1, cpf), criarVeiculo());
        String servicoId = criarServico(100.0);
        String insumoId = criarInsumo(50.0); // estoque nasce zerado

        assertSucesso(iniciarDiagnostico(ordem.id()), "Iniciar diagnóstico");
        assertSucesso(adicionarServico(ordem.id(), servicoId, 1), "Adicionar serviço");
        assertSucesso(adicionarInsumo(ordem.id(), insumoId, 4), "Adicionar insumo");
        assertSucesso(finalizarOrcamento(ordem.id(), "Necessária troca de peças"), "Finalizar orçamento");

        assertSucesso(aprovar(ordem, cpf), "Aprovar orçamento");
        assertThat(statusDa(ordem.id())).isEqualTo("AGUARDANDO_PECAS");

        // Entrada de estoque publica evento que libera as OS que aguardavam peças
        assertSucesso(entradaEstoque(insumoId, 10), "Entrada de estoque");
        aguardarStatus(ordem.id(), "APROVADA");
    }

    // ------------------------------------------------------------------ regras de bloqueio

    @Test
    @DisplayName("Não finaliza orçamento sem diagnóstico (DOM-050)")
    void orcamentoSemDiagnostico() {
        OrdemAberta ordem = abrirOrdemServico(criarClientePessoaFisica(), criarVeiculo());
        assertSucesso(iniciarDiagnostico(ordem.id()), "Iniciar diagnóstico");
        assertSucesso(adicionarServico(ordem.id(), criarServico(100.0), 1), "Adicionar serviço");

        Resposta resposta = finalizarOrcamento(ordem.id(), null);

        assertErroDeDominio(resposta, "DOM-50");
    }

    @Test
    @DisplayName("Não finaliza orçamento sem nenhum serviço (DOM-051)")
    void orcamentoSemServico() {
        OrdemAberta ordem = abrirOrdemServico(criarClientePessoaFisica(), criarVeiculo());
        assertSucesso(iniciarDiagnostico(ordem.id()), "Iniciar diagnóstico");

        Resposta resposta = finalizarOrcamento(ordem.id(), "Diagnóstico qualquer");

        assertErroDeDominio(resposta, "DOM-051");
    }

    @Test
    @DisplayName("Não inicia diagnóstico duas vezes (DOM-036)")
    void diagnosticoDuplicado() {
        OrdemAberta ordem = abrirOrdemServico(criarClientePessoaFisica(), criarVeiculo());
        assertSucesso(iniciarDiagnostico(ordem.id()), "Iniciar diagnóstico");

        Resposta segunda = iniciarDiagnostico(ordem.id());

        assertErroDeDominio(segunda, "DOM-036");
    }

    @Test
    @DisplayName("Não inicia serviço de OS que não foi aprovada (DOM-061)")
    void iniciarServicoSemAprovacao() {
        OrdemAberta ordem = abrirOrdemServico(criarClientePessoaFisica(), criarVeiculo());

        Resposta resposta = iniciarServico(ordem.id());

        assertErroDeDominio(resposta, "DOM-061");
    }

    @Test
    @DisplayName("Aprovação com código de acesso errado é recusada (DOM-071)")
    void aprovarComCodigoErrado() {
        String cpf = GeradorDados.cpfValido();
        OrdemAberta ordem = abrirOrdemServico(criarCliente(1, cpf), criarVeiculo());
        prepararOrcamentoAguardandoAprovacao(ordem, 10);

        Resposta resposta = aprovar(new OrdemAberta(ordem.id(), ordem.numero(), "CODIGO-ERRADO"), cpf);

        assertErroDeDominio(resposta, "DOM-071");
        assertThat(statusDa(ordem.id())).isEqualTo("AGUARDANDO_APROVACAO");
    }

    @Test
    @DisplayName("Aprovação por CPF de outro cliente é recusada (DOM-059)")
    void aprovarPorOutroCliente() {
        OrdemAberta ordem = abrirOrdemServico(criarClientePessoaFisica(), criarVeiculo());
        prepararOrcamentoAguardandoAprovacao(ordem, 10);
        String cpfDeOutroCliente = GeradorDados.cpfValido();
        criarCliente(1, cpfDeOutroCliente);

        Resposta resposta = aprovar(ordem, cpfDeOutroCliente);

        assertErroDeDominio(resposta, "DOM-059");
        assertThat(statusDa(ordem.id())).isEqualTo("AGUARDANDO_APROVACAO");
    }

    @Test
    @DisplayName("Aprovação de OS inexistente é recusada (DOM-055)")
    void aprovarOsInexistente() {
        String cpf = GeradorDados.cpfValido();
        criarCliente(1, cpf);

        Resposta resposta = aprovar(new OrdemAberta("x", "OS-0000-000000", "ABCD1234"), cpf);

        assertErroDeDominio(resposta, "DOM-055");
    }

    @Test
    @DisplayName("Consulta pública exige código de acesso correto")
    void consultaPublicaComCodigoErrado() {
        String cpf = GeradorDados.cpfValido();
        OrdemAberta ordem = abrirOrdemServico(criarCliente(1, cpf), criarVeiculo());

        Resposta resposta = api.get("/api/ordens-servico/cliente/detalhamento?numeroOrdemServico=" + ordem.numero()
                + "&codigoVerificacao=ERRADO&cpfCnpj=" + cpf, null);

        assertThat(resposta.status()).as(resposta.toString()).isGreaterThanOrEqualTo(400);
    }

    @Test
    @DisplayName("Detalhar OS inexistente não retorna sucesso")
    void detalharOsInexistente() {
        Resposta resposta = api.get("/api/ordens-servico/" + GeradorDados.uuidInexistente(), tokenAtendente());

        assertThat(resposta.status()).as(resposta.toString()).isIn(404, 409);
    }
}
