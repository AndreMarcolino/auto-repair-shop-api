package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.Resposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Autenticação e autorização (JWT + papéis)")
class AutenticacaoIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Atendente e mecânico fazem login e recebem um token Bearer")
    void loginComCredenciaisValidas() {
        Resposta atendente = login(EMAIL_ATENDENTE, SENHA_DEMO);
        Resposta mecanico = login(EMAIL_MECANICO, SENHA_DEMO);

        assertThat(atendente.status()).as(atendente.toString()).isEqualTo(200);
        assertThat(atendente.texto("$.tokenType")).isEqualTo("Bearer");
        assertThat(atendente.texto("$.accessToken")).isNotBlank();
        assertThat(mecanico.status()).as(mecanico.toString()).isEqualTo(200);
        assertThat(mecanico.texto("$.accessToken")).isNotBlank();
    }

    @Test
    @DisplayName("Login com senha errada não gera token")
    void loginComSenhaErradaNaoGeraToken() {
        Resposta resposta = login(EMAIL_ATENDENTE, "senha-errada");

        // Não exige 401 aqui (ver DefeitosConhecidosIntegrationTest); garante apenas que não autenticou.
        assertThat(resposta.status()).as(resposta.toString()).isGreaterThanOrEqualTo(400);
        assertThat(resposta.corpo()).doesNotContain("accessToken");
    }

    @Test
    @DisplayName("Endpoint protegido sem token responde 401")
    void semTokenRetorna401() {
        Resposta resposta = api.get("/api/ordens-servico", null);

        assertThat(resposta.status()).isEqualTo(401);
    }

    @Test
    @DisplayName("Token inválido responde 401")
    void tokenInvalidoRetorna401() {
        Resposta resposta = api.get("/api/ordens-servico", "token.invalido.qualquer");

        assertThat(resposta.status()).isEqualTo(401);
    }

    @Test
    @DisplayName("Mecânico não pode abrir OS (somente atendente): 403")
    void mecanicoNaoAbreOrdemServico() {
        Resposta resposta = api.post("/api/ordens-servico", tokenMecanico(),
                "{\"clienteId\":\"" + criarClientePessoaFisica() + "\",\"veiculoId\":\"" + criarVeiculo()
                        + "\",\"problemaRelatado\":\"Teste\"}");

        assertThat(resposta.status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Atendente não pode iniciar diagnóstico (somente mecânico): 403")
    void atendenteNaoIniciaDiagnostico() {
        Resposta resposta = api.patch("/api/ordens-servico/" + java.util.UUID.randomUUID() + "/iniciar-diagnostico",
                tokenAtendente(), null);

        assertThat(resposta.status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Atendente e mecânico podem listar ordens de serviço")
    void ambosPodemListar() {
        assertThat(api.get("/api/ordens-servico", tokenAtendente()).status()).isEqualTo(200);
        assertThat(api.get("/api/ordens-servico", tokenMecanico()).status()).isEqualTo(200);
    }
}
