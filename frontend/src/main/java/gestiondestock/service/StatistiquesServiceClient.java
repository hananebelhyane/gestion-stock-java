package gestiondestock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.dto.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class StatistiquesServiceClient {

    private static final String BASE_URL = "http://localhost:8082/api/statistiques";
    private final ObjectMapper mapper = new ObjectMapper();


    private HttpURLConnection createConnection(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }


    public StatistiquesVentesDTO getStatistiquesVentes() {
        try {
            HttpURLConnection conn = createConnection("/ventes");
            if (conn.getResponseCode() == 200) {
                return mapper.readValue(conn.getInputStream(), StatistiquesVentesDTO.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public StatistiquesStockDTO getStatistiquesStock() {
        try {
            HttpURLConnection conn = createConnection("/stock");
            if (conn.getResponseCode() == 200) {
                return mapper.readValue(conn.getInputStream(), StatistiquesStockDTO.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public EvolutionVentesDTO getEvolutionVentes() {
        try {
            HttpURLConnection conn = createConnection("/evolution-ventes");
            if (conn.getResponseCode() == 200) {
                return mapper.readValue(conn.getInputStream(), EvolutionVentesDTO.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<ProduitVenduDTO> getProduitsLesPlusVendus() {
        try {
            HttpURLConnection conn = createConnection("/top-produits");
            if (conn.getResponseCode() == 200) {
                ProduitVenduDTO[] produits = mapper.readValue(conn.getInputStream(), ProduitVenduDTO[].class);
                return Arrays.asList(produits);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }


    public List<ClientTopDTO> getTopClients() {
        try {
            HttpURLConnection conn = createConnection("/top-clients");
            if (conn.getResponseCode() == 200) {
                ClientTopDTO[] clients = mapper.readValue(conn.getInputStream(), ClientTopDTO[].class);
                return Arrays.asList(clients);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }


    public StatistiquesGeneralesDTO getStatistiquesGenerales() {
        try {
            HttpURLConnection conn = createConnection("/generales"); // Assurez-vous que le backend expose /generales
            if (conn.getResponseCode() == 200) {
                return mapper.readValue(conn.getInputStream(), StatistiquesGeneralesDTO.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
