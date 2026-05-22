package com.crud.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Serviço para consumo da API pública ViaCEP.
 * URL: https://viacep.com.br/ws/{cep}/json/
 *
 * Utilizada para preencher automaticamente o endereço do cliente
 * a partir do CEP informado.
 */
public class ViaCepService {

    private static final String BASE_URL  = "https://viacep.com.br/ws/%s/json/";
    private static final int    TIMEOUT_S = 10;

    private final HttpClient    httpClient;
    private final ObjectMapper  objectMapper;

    public ViaCepService() {
        this.httpClient   = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_S))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ─── DTO interno para os dados do endereço ─────────────────────────────────

    /** Dados de endereço retornados pela API ViaCEP. */
    public static class EnderecoDTO {
        public final String cep;
        public final String logradouro;
        public final String bairro;
        public final String cidade;
        public final String uf;

        public EnderecoDTO(String cep, String logradouro, String bairro, String cidade, String uf) {
            this.cep        = cep;
            this.logradouro = logradouro;
            this.bairro     = bairro;
            this.cidade     = cidade;
            this.uf         = uf;
        }

        @Override
        public String toString() {
            return String.format("%s, %s - %s/%s", logradouro, bairro, cidade, uf);
        }
    }

    // ─── Método principal ──────────────────────────────────────────────────────

    /**
     * Busca o endereço correspondente ao CEP informado.
     *
     * @param cep CEP no formato "01310100" ou "01310-100"
     * @return EnderecoDTO com os dados do endereço
     * @throws ViaCepException se o CEP for inválido ou a API não estiver disponível
     */
    public EnderecoDTO buscarEndereco(String cep) throws ViaCepException {
        // Normaliza: remove hífen e espaços
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new ViaCepException("CEP inválido. Informe exatamente 8 dígitos.");
        }

        String url = String.format(BASE_URL, cepLimpo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_S))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ViaCepException("Serviço ViaCEP indisponível (HTTP " + response.statusCode() + ").");
            }

            JsonNode json = objectMapper.readTree(response.body());

            // ViaCEP retorna {"erro": true} para CEPs inexistentes
            if (json.has("erro") && json.get("erro").asBoolean()) {
                throw new ViaCepException("CEP " + cepLimpo + " não encontrado na base dos Correios.");
            }

            return new EnderecoDTO(
                formatarCep(cepLimpo),
                json.path("logradouro").asText(""),
                json.path("bairro").asText(""),
                json.path("localidade").asText(""),
                json.path("uf").asText("")
            );

        } catch (IOException e) {
            throw new ViaCepException("Erro de conexão com a API ViaCEP: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ViaCepException("Consulta ao ViaCEP foi interrompida.");
        }
    }

    /** Formata o CEP no padrão "XXXXX-XXX". */
    private String formatarCep(String cepLimpo) {
        return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
    }

    // ─── Exceção customizada ───────────────────────────────────────────────────

    /** Exceção lançada quando a consulta ao ViaCEP falha. */
    public static class ViaCepException extends Exception {
        public ViaCepException(String mensagem) {
            super(mensagem);
        }
    }
}
