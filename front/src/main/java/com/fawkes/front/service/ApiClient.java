package com.fawkes.front.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    // ---------------- Basic Auth ----------------
    private static String basicAuthHeader() {
        String credentials = "admin:fawkes2026";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    // ---------------- Stock ----------------
    public static JsonNode listStock() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/stock"))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    public static JsonNode registerInput(Long stockId, Long productId, Integer quantity) throws Exception {
        String url = BASE_URL + "/api/stock/movements/input"
                + "?stockId=" + stockId
                + "&productId=" + productId
                + "&quantity=" + quantity;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    public static JsonNode registerOutput(Long stockId, Long productId, Integer quantity) throws Exception {
        String url = BASE_URL + "/api/stock/movements/output"
                + "?stockId=" + stockId
                + "&productId=" + productId
                + "&quantity=" + quantity;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    // ---------------- Generic GET ----------------
    public static JsonNode get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    // ---------------- Generic POST ----------------
    public static JsonNode post(String path, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    // ---------------- Generic PUT ----------------
    public static JsonNode put(String path, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
        return mapper.readTree(response.body());
    }

    // ---------------- Generic DELETE ----------------
    public static void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .header("Accept", "application/json")
                .header("Authorization", basicAuthHeader())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertOk(response);
    }

    // ---------------- Helper ----------------
    private static void assertOk(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Erro HTTP " + response.statusCode() + ": " + response.body());
        }
    }
}