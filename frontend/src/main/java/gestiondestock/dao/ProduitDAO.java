package gestiondestock.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gestiondestock.model.Produit;
import gestiondestock.model.ProduitDTO;
import gestiondestock.model.Session;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    private static final String BASE_URL = "http://localhost:8080/api/produits";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static List<Produit> getAll() throws IOException, InterruptedException {
        HttpRequest req = baseRequest(BASE_URL)
                .GET()
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200) throw new IOException("HTTP " + resp.statusCode());

        Type listType = new TypeToken<ArrayList<ProduitDTO>>(){}.getType();
        List<ProduitDTO> dtos = GSON.fromJson(resp.body(), listType);

        List<Produit> produits = new ArrayList<>();
        for (ProduitDTO dto : dtos) produits.add(Produit.fromDTO(dto));
        return produits;
    }

    public static Produit save(Produit produit) throws IOException, InterruptedException {
        String json = GSON.toJson(produit.toDTO());

        HttpRequest req = baseRequest(BASE_URL)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 201 && resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode() + " - " + resp.body());

        ProduitDTO dto = GSON.fromJson(resp.body(), ProduitDTO.class);
        return Produit.fromDTO(dto);
    }

    public static Produit update(Produit produit) throws IOException, InterruptedException {
        if (produit.getId() == null || produit.getId().isEmpty())
            throw new IllegalArgumentException("Produit id manquant");

        String json = GSON.toJson(produit.toDTO());

        HttpRequest req = baseRequest(BASE_URL + "/" + produit.getId())
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200) throw new IOException("HTTP " + resp.statusCode() + " - " + resp.body());

        ProduitDTO dto = GSON.fromJson(resp.body(), ProduitDTO.class);
        return Produit.fromDTO(dto);
    }

    public static void delete(String id) throws IOException, InterruptedException {
        HttpRequest req = baseRequest(BASE_URL + "/" + id)
            .DELETE()
            .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 204 && resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode() + " - " + resp.body());
    }

    public static void exportToCSV(String filename) throws IOException, InterruptedException {
        List<Produit> produits = getAll();
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Nom,Description,Prix,Categorie,Fournisseur\n");
            for (Produit p : produits) {
                writer.write(String.format("%s,%s,%.2f,%s,%s%n",
                        p.getNom(),
                        p.getDescription(),
                        p.getPrixUnitaire() != null ? p.getPrixUnitaire() : 0.0,
                        p.getCategorie() != null ? p.getCategorie().getNom() : "",
                        p.getFournisseur() != null ? p.getFournisseur().getNom() : ""
                ));
            }
        }
    }

    private static HttpRequest.Builder baseRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        String token = Session.get().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }
}
