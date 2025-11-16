package gestiondestock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpClientService {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ---------------------- READ RESPONSE ----------------------
    private static String readResponse(CloseableHttpResponse response) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    // ---------------------- GET ----------------------
    public static <T> T get(String url, Class<T> type, String role) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpGet req = new HttpGet(url);
            req.addHeader("role", role);

            try (CloseableHttpResponse resp = client.execute(req)) {
                String json = readResponse(resp);
                return mapper.readValue(json, type);
            }
        }
    }

    // ---------------------- POST ----------------------
    public static <T> T post(String url, Object data, Class<T> returnType, String role) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost req = new HttpPost(url);
            req.addHeader("Content-Type", "application/json");
            req.addHeader("role", role);

            String json = mapper.writeValueAsString(data);
            req.setEntity(new StringEntity(json));

            try (CloseableHttpResponse resp = client.execute(req)) {
                String body = readResponse(resp);
                return mapper.readValue(body, returnType);
            }
        }
    }

    // ---------------------- PUT ----------------------
    public static void put(String url, Object data, String role) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPut req = new HttpPut(url);
            req.addHeader("Content-Type", "application/json");
            req.addHeader("role", role);

            String json = mapper.writeValueAsString(data);
            req.setEntity(new StringEntity(json));

            try (CloseableHttpResponse resp = client.execute(req)) {
                // aucune réponse attendue → donc rien à retourner
            }
        }
    }

    // ---------------------- DELETE ----------------------
    public static void delete(String url, String role) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpDelete req = new HttpDelete(url);
            req.addHeader("role", role);

            try (CloseableHttpResponse resp = client.execute(req)) {
                // si le backend renvoie une erreur HTTP
                int code = resp.getCode();
                if (code >= 400) {
                    throw new RuntimeException("Erreur DELETE, code: " + code);
                }
            }
        }
    }
}
