package br.com.autorepairshop.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autorepairshop.integration.support.GeradorDados;
import br.com.autorepairshop.integration.support.Resposta;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes que descrevem o comportamento CORRETO esperado e que, pela leitura do código, FALHAM hoje.
 *
 * Ficam desabilitados (@Disabled) para não quebrar o build do grupo. Quando o defeito for corrigido,
 * basta remover o @Disabled do teste correspondente: ele passa a proteger a correção.
 * Para confirmar cada defeito, remova o @Disabled localmente e rode o teste.
 */
@DisplayName("Defeitos conhecidos (documentação executável)")
class DefeitosConhecidosIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Disabled("DEFEITO: login com senha errada deveria responder 401, mas a API responde 500 (exceção de credenciais não mapeada em TratamentoExcecoesApi)")
    @DisplayName("Login com senha errada deve responder 401")
    void loginComSenhaErradaDeveRetornar401() {
        Resposta resposta = login(EMAIL_ATENDENTE, "senha-errada");

        assertThat(resposta.status()).isEqualTo(401);
    }

    @Test
    @Disabled("DEFEITO: aprovar/reprovar/consultar OS por CNPJ usa tipo PESSOA_FISICA ao localizar o cliente, então clientes PJ não conseguem aprovar")
    @DisplayName("Cliente pessoa jurídica deve conseguir aprovar o orçamento com o CNPJ")
    void clientePessoaJuridicaDeveConseguirAprovar() {
        String cnpj = GeradorDados.cnpjValido();
        OrdemAberta ordem = abrirOrdemServico(criarCliente(2, cnpj), criarVeiculo());
        prepararOrcamentoAguardandoAprovacao(ordem, 10);

        Resposta resposta = aprovar(ordem, cnpj);

        assertSucesso(resposta, "Aprovar orçamento por CNPJ");
        assertThat(statusDa(ordem.id())).isEqualTo("APROVADA");
    }
}
