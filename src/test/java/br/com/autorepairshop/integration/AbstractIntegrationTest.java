package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.ApiClient;
import br.com.autorepairshop.integration.support.GeradorDados;
import br.com.autorepairshop.integration.support.Resposta;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base dos testes de integração.
 *
 * Sobe a aplicação inteira (controllers, casos de uso, domínio, JPA, Flyway, segurança JWT) em uma porta
 * aleatória, conectada a um PostgreSQL real (Testcontainers, ver application-test.properties).
 * Os testes conversam com a API apenas por HTTP, como um cliente real.
 *
 * Funcionários de demonstração criados pela migration V17 (README: senha 123456).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final String EMAIL_ATENDENTE = "atendente@auto-repair-shop.com.br";
    protected static final String EMAIL_MECANICO = "mecanico@auto-repair-shop.com.br";
    protected static final String SENHA_DEMO = "123456";

    /** Propriedade definida pelo Spring Boot quando o servidor sobe em porta aleatória. */
    @Value("${local.server.port}")
    private int porta;

    protected ApiClient api;

    private static String tokenAtendente;
    private static String tokenMecanico;

    @BeforeEach
    void prepararCliente() {
        api = new ApiClient(porta);
    }

    // ------------------------------------------------------------------ autenticação

    protected Resposta login(String email, String senha) {
        String json = "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}";
        return api.getComCorpo("/api/autenticacao/login", null, json);
    }

    protected String tokenAtendente() {
        if (tokenAtendente == null) {
            tokenAtendente = obterToken(EMAIL_ATENDENTE);
        }
        return tokenAtendente;
    }

    protected String tokenMecanico() {
        if (tokenMecanico == null) {
            tokenMecanico = obterToken(EMAIL_MECANICO);
        }
        return tokenMecanico;
    }

    private String obterToken(String email) {
        Resposta resposta = login(email, SENHA_DEMO);
        assertThat(resposta.status())
                .as("Login do funcionário de demonstração %s: %s", email, resposta)
                .isEqualTo(200);
        return resposta.texto("$.accessToken");
    }

    // ------------------------------------------------------------------ cadastros

    protected String criarClientePessoaFisica() {
        return criarCliente(1, GeradorDados.cpfValido());
    }

    protected String criarClientePessoaJuridica() {
        return criarCliente(2, GeradorDados.cnpjValido());
    }

    protected String criarCliente(int tipoPessoa, String cpfCnpj) {
        Resposta resposta = api.post("/api/clientes", tokenAtendente(), jsonCliente(tipoPessoa, cpfCnpj));
        assertThat(resposta.status()).as("Criar cliente: %s", resposta).isEqualTo(200);
        return resposta.corpoSemAspas();
    }

    protected String jsonCliente(int tipoPessoa, String cpfCnpj) {
        return "{\"nome\":\"Cliente Teste\",\"tipoPessoa\":" + tipoPessoa
                + ",\"cpfCnpj\":\"" + cpfCnpj + "\""
                + ",\"telefone\":\"11999998888\",\"email\":\"" + GeradorDados.emailUnico() + "\"}";
    }

    protected String criarVeiculo() {
        Resposta resposta = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(GeradorDados.placaAntiga(), 2020));
        assertThat(resposta.status()).as("Criar veículo: %s", resposta).isEqualTo(200);
        return resposta.corpoSemAspas();
    }

    protected String jsonVeiculo(String placa, int ano) {
        return "{\"placa\":\"" + placa + "\",\"marca\":\"Fiat\",\"modelo\":\"Uno\",\"anoFabricacao\":" + ano + "}";
    }

    protected String criarServico(double valorReferencia) {
        String json = "{\"descricao\":\"" + GeradorDados.descricaoUnica("Servico") + "\",\"valorReferencia\":" + valorReferencia + "}";
        Resposta resposta = api.post("/api/servicos", tokenAtendente(), json);
        assertThat(resposta.status()).as("Criar serviço: %s", resposta).isEqualTo(200);
        return resposta.corpoSemAspas();
    }

    protected String criarInsumo(double valorReferencia) {
        String json = "{\"descricao\":\"" + GeradorDados.descricaoUnica("Insumo") + "\",\"valorReferencia\":" + valorReferencia + "}";
        Resposta resposta = api.post("/api/insumos", tokenAtendente(), json);
        assertThat(resposta.status()).as("Criar insumo: %s", resposta).isEqualTo(200);
        return resposta.corpoSemAspas();
    }

    protected Resposta entradaEstoque(String insumoId, double quantidade) {
        String json = "{\"insumoId\":\"" + insumoId + "\",\"quantidade\":" + quantidade + "}";
        return api.post("/api/estoque/entrada", tokenAtendente(), json);
    }

    // ------------------------------------------------------------------ ordem de serviço

    /** Dados devolvidos ao abrir uma OS: o id interno, o número e o código de acesso do cliente. */
    protected record OrdemAberta(String id, String numero, String codigoAcesso) {
    }

    protected Resposta abrirOrdemServicoResposta(String clienteId, String veiculoId) {
        String json = "{\"clienteId\":\"" + clienteId + "\",\"veiculoId\":\"" + veiculoId
                + "\",\"problemaRelatado\":\"Barulho no motor ao acelerar.\"}";
        return api.post("/api/ordens-servico", tokenAtendente(), json);
    }

    protected OrdemAberta abrirOrdemServico(String clienteId, String veiculoId) {
        Resposta resposta = abrirOrdemServicoResposta(clienteId, veiculoId);
        assertThat(resposta.status()).as("Abrir OS: %s", resposta).isEqualTo(200);

        String numero = resposta.texto("$.numero");
        String codigo = resposta.texto("$.codigoAcesso");

        // A resposta da abertura não traz o id; ele é localizado pela listagem, pelo número da OS.
        Resposta lista = api.get("/api/ordens-servico", tokenAtendente());
        assertThat(lista.status()).as("Listar OS: %s", lista).isEqualTo(200);
        List<String> ids = lista.campo("$.ordensServico[?(@.numero=='" + numero + "')].id");
        assertThat(ids).as("OS %s deve aparecer na listagem", numero).hasSize(1);

        return new OrdemAberta(ids.get(0), numero, codigo);
    }

    protected Resposta detalhar(String ordemId) {
        return api.get("/api/ordens-servico/" + ordemId, tokenAtendente());
    }

    protected String statusDa(String ordemId) {
        return detalhar(ordemId).texto("$.status");
    }

    /**
     * Aguarda a OS chegar ao status esperado. Algumas transições são feitas por eventos de domínio
     * executados após o commit (ex.: FINALIZADA → AGUARDANDO_PAGAMENTO), por isso há uma espera curta.
     */
    protected void aguardarStatus(String ordemId, String esperado) {
        String atual = null;
        long limite = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < limite) {
            atual = statusDa(ordemId);
            if (esperado.equals(atual)) {
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        assertThat(atual).as("Status da OS %s", ordemId).isEqualTo(esperado);
    }

    protected Resposta iniciarDiagnostico(String ordemId) {
        return api.patch("/api/ordens-servico/" + ordemId + "/iniciar-diagnostico", tokenMecanico(), null);
    }

    protected Resposta adicionarServico(String ordemId, String servicoId, int quantidade) {
        String json = "{\"servicos\":[{\"idServico\":\"" + servicoId + "\",\"quantidade\":" + quantidade + "}]}";
        return api.post("/api/ordens-servico/" + ordemId + "/orcamento/adicionar-servicos", tokenMecanico(), json);
    }

    protected Resposta adicionarInsumo(String ordemId, String insumoId, int quantidade) {
        String json = "{\"insumos\":[{\"idInsumo\":\"" + insumoId + "\",\"quantidade\":" + quantidade + "}]}";
        return api.post("/api/ordens-servico/" + ordemId + "/orcamento/adicionar-insumos", tokenMecanico(), json);
    }

    protected Resposta finalizarOrcamento(String ordemId, String diagnostico) {
        String json = "{\"diagnostico\":" + (diagnostico == null ? "null" : "\"" + diagnostico + "\"") + "}";
        return api.post("/api/ordens-servico/" + ordemId + "/orcamento/finalizar", tokenMecanico(), json);
    }

    /** Aprovação/reprovação feitas pelo cliente: endpoints públicos, identificados por número, código e CPF/CNPJ. */
    protected Resposta aprovar(OrdemAberta ordem, String cpfCnpj) {
        return api.post("/api/ordens-servico/aprovar", null, jsonDecisaoCliente(ordem, cpfCnpj));
    }

    protected Resposta reprovar(OrdemAberta ordem, String cpfCnpj) {
        return api.post("/api/ordens-servico/reprovar", null, jsonDecisaoCliente(ordem, cpfCnpj));
    }

    private String jsonDecisaoCliente(OrdemAberta ordem, String cpfCnpj) {
        return "{\"numeroOrdemServico\":\"" + ordem.numero() + "\",\"codigoVerificacao\":\"" + ordem.codigoAcesso()
                + "\",\"cpfCnpj\":\"" + cpfCnpj + "\"}";
    }

    protected Resposta iniciarServico(String ordemId) {
        return api.post("/api/ordens-servico/" + ordemId + "/iniciar-servico", tokenMecanico(), null);
    }

    protected Resposta finalizarServico(String ordemId) {
        return api.post("/api/ordens-servico/" + ordemId + "/finalizar-servico", tokenMecanico(), null);
    }

    protected Resposta registrarPagamento(String ordemId) {
        return api.post("/api/ordens-servico/" + ordemId + "/registrar-pagamento", tokenAtendente(), null);
    }

    protected Resposta entregarVeiculo(String ordemId) {
        return api.post("/api/ordens-servico/" + ordemId + "/entregar-veiculo", tokenAtendente(), null);
    }

    protected void assertSucesso(Resposta resposta, String acao) {
        assertThat(resposta.sucesso()).as("%s deveria ter sucesso, mas retornou: %s", acao, resposta).isTrue();
    }

    protected void assertErroDeDominio(Resposta resposta, String codigoEsperado) {
        assertThat(resposta.status()).as("Resposta: %s", resposta).isEqualTo(409);
        assertThat(resposta.codigoDoErro()).as("Código do erro. Resposta: %s", resposta).isEqualTo(codigoEsperado);
    }

    /**
     * Leva uma OS recém-aberta até AGUARDANDO_APROVACAO com orçamento de 1 serviço (R$ 100,00)
     * e 4 insumos (R$ 50,00 cada) = R$ 300,00, com estoque suficiente.
     */
    protected void prepararOrcamentoAguardandoAprovacao(OrdemAberta ordem, double estoqueDoInsumo) {
        String servicoId = criarServico(100.0);
        String insumoId = criarInsumo(50.0);
        if (estoqueDoInsumo > 0) {
            assertSucesso(entradaEstoque(insumoId, estoqueDoInsumo), "Entrada de estoque");
        }

        assertSucesso(iniciarDiagnostico(ordem.id()), "Iniciar diagnóstico");
        assertSucesso(adicionarServico(ordem.id(), servicoId, 1), "Adicionar serviço");
        assertSucesso(adicionarInsumo(ordem.id(), insumoId, 4), "Adicionar insumo");
        assertSucesso(finalizarOrcamento(ordem.id(), "Troca de peças e revisão geral"), "Finalizar orçamento");
        assertThat(statusDa(ordem.id())).isEqualTo("AGUARDANDO_APROVACAO");
    }
}
