package com.fawkes.front.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode listStock() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/stock"))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readTree(response.body());
        } else {
            throw new RuntimeException("Erro [HTTP " + response.statusCode() + "]: " + response.body());
        }
    }

    public static JsonNode registerInput(long stockId, long productId, int quantity) throws Exception {
        String url = BASE_URL + "/api/stock/movements/input"
                + "?stockId=" + stockId
                + "&productId=" + productId
                + "&quantity=" + quantity;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readTree(response.body());
        } else {
            throw new RuntimeException("Erro [HTTP " + response.statusCode() + "]: " + response.body());
        }
    }

    public static JsonNode registerOutput(long stockId, long productId, int quantity) throws Exception {
        String url = BASE_URL + "/api/stock/movements/output"
                + "?stockId=" + stockId
                + "&productId=" + productId
                + "&quantity=" + quantity;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readTree(response.body());
        } else {
            throw new RuntimeException("Erro [HTTP " + response.statusCode() + "]: " + response.body());
        }
    }
}