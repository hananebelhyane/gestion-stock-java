package gestiondestock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gestiondestock.dto.CommandeDTO;
import gestiondestock.model.Session;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class CommandeServiceClient {

    private static final String BASE_URL = "http://localhost:8080/api/commandes";
    private final ObjectMapper mapper;

    public CommandeServiceClient() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    private HttpURLConnection createConnection(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        
        String token = Session.get().getToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        return connection;
    }

    public List<CommandeDTO> getCommandesClients() {
        return fetchCommandes("/clients");
    }

    public List<CommandeDTO> getCommandesFournisseurs() {
        return fetchCommandes("/fournisseurs");
    }

    private List<CommandeDTO> fetchCommandes(String endpoint) {
        try {
            HttpURLConnection conn = createConnection(endpoint);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return mapper.readValue(conn.getInputStream(), new TypeReference<List<CommandeDTO>>() {});
            } else {
                System.err.println("Erreur HTTP " + responseCode + " pour " + endpoint);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
