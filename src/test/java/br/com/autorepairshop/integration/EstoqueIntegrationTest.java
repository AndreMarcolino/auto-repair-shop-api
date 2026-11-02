package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.GeradorDados;
import br.com.autorepairshop.integration.support.Resposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Estoque de insumos")
class EstoqueIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Entrada de estoque de insumo existente retorna 204")
    void entradaDeEstoque() {
        String insumoId = criarInsumo(20.0);

        Resposta resposta = entradaEstoque(insumoId, 15);

        assertThat(resposta.status()).as(resposta.toString()).isEqualTo(204);
    }

    @Test
    @DisplayName("Quantidade zero é recusada pela validação (400)")
    void quantidadeZero() {
        String insumoId = criarInsumo(20.0);

        Resposta resposta = entradaEstoque(insumoId, 0);

        assertThat(resposta.status()).as(resposta.toString()).isEqualTo(400);
    }

    @Test
    @DisplayName("Entrada de estoque para insumo inexistente não retorna sucesso")
    void insumoInexistente() {
        Resposta resposta = entradaEstoque(GeradorDados.uuidInexistente(), 5);

        assertThat(resposta.sucesso()).as(resposta.toString()).isFalse();
    }

    @Test
    @DisplayName("Mecânico não registra entrada de estoque (somente atendente): 403")
    void mecanicoNaoRegistraEntrada() {
        String insumoId = criarInsumo(20.0);
        String json = "{\"insumoId\":\"" + insumoId + "\",\"quantidade\":5}";

        Resposta resposta = api.post("/api/estoque/entrada", tokenMecanico(), json);

        assertThat(resposta.status()).isEqualTo(403);
    }
}
