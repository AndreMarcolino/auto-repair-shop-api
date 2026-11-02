package br.com.autorepairshop.integration.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * Cliente HTTP mínimo (somente JDK) usado pelos testes de integração.
 *
 * Os testes sobem a aplicação de verdade (servidor web em porta aleatória + PostgreSQL real) e
 * fazem requisições HTTP reais, exatamente como um cliente da API faria.
 */
public final class ApiClient {

    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String urlBase;

    public ApiClient(int porta) {
        this.urlBase = "http://localhost:" + porta;
    }

    public Resposta get(String caminho, String token) {
        return enviar("GET", caminho, token, null);
    }

    /** A API de login é um GET com corpo JSON (incomum, mas é o contrato atual da API). */
    public Resposta getComCorpo(String caminho, String token, String json) {
        return enviar("GET", caminho, token, json);
    }

    public Resposta post(String caminho, String token, String json) {
        return enviar("POST", caminho, token, json);
    }

    public Resposta patch(String caminho, String token, String json) {
        return enviar("PATCH", caminho, token, json);
    }

    public Resposta delete(String caminho, String token) {
        return enviar("DELETE", caminho, token, null);
    }

    private Resposta enviar(String metodo, String caminho, String token, String json) {
        HttpRequest.Builder requisicao = HttpRequest.newBuilder(URI.create(urlBase + caminho))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json");

        if (token != null) {
            requisicao.header("Authorization", "Bearer " + token);
        }

        requisicao.method(metodo, json == null
                ? BodyPublishers.noBody()
                : BodyPublishers.ofString(json));

        try {
            HttpResponse<String> resposta = http.send(requisicao.build(), BodyHandlers.ofString());
            return new Resposta(resposta.statusCode(), resposta.body());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao chamar " + metodo + " " + caminho, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chamada interrompida: " + metodo + " " + caminho, e);
        }
    }
}
