package gestiondestock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.model.Stock;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.List;
import java.util.UUID;

public class StockService {

    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String userRole;

    public StockService(String userRole) {
        this.userRole = userRole;
    }

    // GET ALL
    public List<Stock> getAllStock() throws Exception {
        HttpGet request = new HttpGet(BASE_URL);
        request.addHeader("role", userRole);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(request);
            var json = new String(response.getEntity().getContent().readAllBytes());

            return mapper.readValue(json, new TypeReference<List<Stock>>() {
            });
        }
    }

    // GET BY PRODUIT ID
    public Stock getStockByProduitId(UUID produitId) throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "/" + produitId);
        request.addHeader("role", userRole);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(request);
            var json = new String(response.getEntity().getContent().readAllBytes());

            return mapper.readValue(json, Stock.class);
        }
    }

    // POST - CREATE
    public Stock createStock(Stock stock) throws Exception {
        HttpPost request = new HttpPost(BASE_URL);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("role", userRole);

        String jsonBody = mapper.writeValueAsString(stock);
        request.setEntity(new StringEntity(jsonBody));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(request);
            var json = new String(response.getEntity().getContent().readAllBytes());

            return mapper.readValue(json, Stock.class);
        }
    }

    // DELETE
    public void deleteStock(UUID stockId) throws Exception {
        HttpDelete request = new HttpDelete(BASE_URL + "/" + stockId);
        request.addHeader("role", userRole);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(request);
        }
    }
}
