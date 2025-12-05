package gestiondestock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.dto.*;
import gestiondestock.model.Session;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class StatistiquesServiceClient {

    private static final String BASE_URL = "http://localhost:8080/api/statistiques";
    private final ObjectMapper mapper = new ObjectMapper();


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
            int code = conn.getResponseCode();
            if (code == 200) {
                EvolutionVentesDTO dto = mapper.readValue(conn.getInputStream(), EvolutionVentesDTO.class);
                if (dto == null) {
                    dto = new EvolutionVentesDTO();
                    dto.setVentesParJour(List.of());
                    dto.setVentesParMois(List.of());
                    dto.setVentesParAnnee(List.of());
                }
                return dto;
            }
            // fallback on non-200
            EvolutionVentesDTO empty = new EvolutionVentesDTO();
            empty.setVentesParJour(List.of());
            empty.setVentesParMois(List.of());
            empty.setVentesParAnnee(List.of());
            return empty;
        } catch (IOException e) {
            e.printStackTrace();
        }
        EvolutionVentesDTO empty = new EvolutionVentesDTO();
        empty.setVentesParJour(List.of());
        empty.setVentesParMois(List.of());
        empty.setVentesParAnnee(List.of());
        return empty;
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
