package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.GeradorDados;
import br.com.autorepairshop.integration.support.Resposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cadastros: clientes, veículos, serviços e insumos")
class CadastrosIntegrationTest extends AbstractIntegrationTest {

    // ------------------------------------------------------------------ clientes

    @Test
    @DisplayName("Cria cliente pessoa física e consulta por id e por CPF")
    void criaEConsultaClientePessoaFisica() {
        String cpf = GeradorDados.cpfValido();
        String id = criarCliente(1, cpf);

        Resposta porId = api.get("/api/clientes/" + id, tokenAtendente());
        Resposta porCpf = api.get("/api/clientes/por-cpf-cnpj?tipoPessoa=1&cpfCnpj=" + cpf, tokenAtendente());

        assertThat(porId.status()).as(porId.toString()).isEqualTo(200);
        assertThat(porId.texto("$.nome")).isEqualTo("Cliente Teste");
        assertThat(porCpf.status()).as(porCpf.toString()).isEqualTo(200);
    }

    @Test
    @DisplayName("Cria cliente pessoa jurídica (CNPJ)")
    void criaClientePessoaJuridica() {
        String id = criarClientePessoaJuridica();

        assertThat(api.get("/api/clientes/" + id, tokenAtendente()).status()).isEqualTo(200);
    }

    @Test
    @DisplayName("CPF inválido é recusado (DOM-003)")
    void cpfInvalido() {
        Resposta resposta = api.post("/api/clientes", tokenAtendente(), jsonCliente(1, "11111111111"));

        assertErroDeDominio(resposta, "DOM-003");
    }

    @Test
    @DisplayName("CPF duplicado é recusado (DOM-004)")
    void cpfDuplicado() {
        String cpf = GeradorDados.cpfValido();
        criarCliente(1, cpf);

        Resposta resposta = api.post("/api/clientes", tokenAtendente(), jsonCliente(1, cpf));

        assertErroDeDominio(resposta, "DOM-004");
    }

    @Test
    @DisplayName("Consulta de cliente inexistente retorna erro de domínio (DOM-007)")
    void clienteNaoEncontrado() {
        Resposta resposta = api.get("/api/clientes/" + GeradorDados.uuidInexistente(), tokenAtendente());

        assertThat(resposta.status()).as(resposta.toString()).isEqualTo(404);
    }

    @Test
    @DisplayName("Id de cliente malformado é recusado (DOM-008)")
    void idClienteInvalido() {
        Resposta resposta = api.get("/api/clientes/nao-e-um-uuid", tokenAtendente());

        assertErroDeDominio(resposta, "DOM-008");
    }

    @Test
    @DisplayName("Desativa cliente; desativar de novo é recusado (DOM-009)")
    void desativaCliente() {
        String id = criarClientePessoaFisica();

        Resposta primeira = api.delete("/api/clientes/" + id, tokenAtendente());
        Resposta segunda = api.delete("/api/clientes/" + id, tokenAtendente());

        assertSucesso(primeira, "Desativar cliente");
        assertErroDeDominio(segunda, "DOM-009");
    }

    @Test
    @DisplayName("Cliente desativado não pode abrir OS (DOM-029)")
    void clienteDesativadoNaoAbreOs() {
        String clienteId = criarClientePessoaFisica();
        String veiculoId = criarVeiculo();
        assertSucesso(api.delete("/api/clientes/" + clienteId, tokenAtendente()), "Desativar cliente");

        Resposta resposta = abrirOrdemServicoResposta(clienteId, veiculoId);

        assertErroDeDominio(resposta, "DOM-029");
    }

    @Test
    @DisplayName("Cadastros exigem autenticação")
    void cadastroSemTokenRetorna401() {
        Resposta resposta = api.post("/api/clientes", null, jsonCliente(1, GeradorDados.cpfValido()));

        assertThat(resposta.status()).isEqualTo(401);
    }

    // ------------------------------------------------------------------ veículos

    @Test
    @DisplayName("Cria veículo (placa antiga e Mercosul) e consulta por id")
    void criaEConsultaVeiculo() {
        Resposta antiga = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(GeradorDados.placaAntiga(), 2018));
        Resposta mercosul = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(GeradorDados.placaMercosul(), 2023));

        assertThat(antiga.status()).as(antiga.toString()).isEqualTo(200);
        assertThat(mercosul.status()).as(mercosul.toString()).isEqualTo(200);
        assertThat(api.get("/api/veiculos/" + antiga.corpoSemAspas(), tokenAtendente()).status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Placa inválida é recusada (DOM-011)")
    void placaInvalida() {
        Resposta resposta = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo("INVALIDA", 2020));

        assertErroDeDominio(resposta, "DOM-011");
    }

    @Test
    @DisplayName("Placa duplicada é recusada (DOM-015)")
    void placaDuplicada() {
        String placa = GeradorDados.placaAntiga();
        assertThat(api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(placa, 2020)).status()).isEqualTo(200);

        Resposta resposta = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(placa, 2020));

        assertErroDeDominio(resposta, "DOM-015");
    }

    @Test
    @DisplayName("Ano de fabricação inválido é recusado (DOM-014)")
    void anoInvalido() {
        Resposta resposta = api.post("/api/veiculos", tokenAtendente(), jsonVeiculo(GeradorDados.placaAntiga(), 1800));

        assertErroDeDominio(resposta, "DOM-014");
    }

    // ------------------------------------------------------------------ serviços e insumos

    @Test
    @DisplayName("Cria e consulta serviço")
    void criaEConsultaServico() {
        String id = criarServico(150.0);

        assertThat(api.get("/api/servicos/" + id, tokenAtendente()).status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Cria e consulta insumo")
    void criaEConsultaInsumo() {
        String id = criarInsumo(35.5);

        assertThat(api.get("/api/insumos/" + id, tokenAtendente()).status()).isEqualTo(200);
    }
}
