package br.com.autorepairshop.integration.support;

import com.jayway.jsonpath.JsonPath;

/**
 * Resposta HTTP de uma chamada à API, com atalhos para ler o JSON devolvido.
 *
 * @param status código HTTP (200, 204, 409...)
 * @param corpo  corpo da resposta como texto (pode ser vazio)
 */
public record Resposta(int status, String corpo) {

    /** Lê um valor do JSON usando JSONPath, por exemplo {@code $.status} ou {@code $.orcamento.valorTotal}. */
    public <T> T campo(String jsonPath) {
        return JsonPath.read(corpo, jsonPath);
    }

    /** Lê um valor do JSON e devolve como texto (ou {@code null} quando o campo for nulo). */
    public String texto(String jsonPath) {
        Object valor = campo(jsonPath);
        return valor == null ? null : valor.toString();
    }

    /** Lê um valor numérico do JSON. */
    public double numero(String jsonPath) {
        Object valor = campo(jsonPath);
        return ((Number) valor).doubleValue();
    }

    /**
     * Código do primeiro erro de domínio. Os erros de regra de negócio (HTTP 409) vêm como lista:
     * {@code [{"codigo":"DOM-003","mensagem":"CPF/CNPJ inválido."}]}.
     */
    public String codigoDoErro() {
        return texto("$[0].codigo");
    }

    /** Corpo sem aspas e sem espaços. Os endpoints de criação devolvem apenas o UUID do registro criado. */
    public String corpoSemAspas() {
        return corpo == null ? "" : corpo.replace("\"", "").trim();
    }

    public boolean sucesso() {
        return status >= 200 && status < 300;
    }

    @Override
    public String toString() {
        return "HTTP " + status + " " + corpo;
    }
}
