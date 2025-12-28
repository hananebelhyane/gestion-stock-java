package gestiondestock.dao;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gestiondestock.model.Produit;
import gestiondestock.model.ProduitDTO;
import gestiondestock.model.Session;

public class ProduitDAO {
    private static final String BASE_URL = "http://localhost:8080/api/produits";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static List<Produit> getAll() throws IOException, InterruptedException {
        return getAll(null);
    }

    public static List<Produit> getAll(String categorieId) throws IOException, InterruptedException {
        String url = BASE_URL;
        if (categorieId != null && !categorieId.isBlank()) {
            url = url + "?categorieId=" + categorieId;
        }

        HttpRequest req = baseRequest(url)
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

    public static void deleteForce(String id) throws IOException, InterruptedException {
        HttpRequest req = baseRequest(BASE_URL + "/" + id + "?force=true")
            .DELETE()
            .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 204 && resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode() + " - " + resp.body());
    }

    public static void exportToCSV(String filename) throws IOException, InterruptedException {
        List<Produit> produits = getAll();
        // Utiliser UTF-8 avec BOM pour Excel
        try (FileWriter writer = new FileWriter(filename, java.nio.charset.StandardCharsets.UTF_8)) {
            // Ajouter le BOM UTF-8 pour Excel
            writer.write('\ufeff');
            
            // En-tête avec point-virgule (meilleur pour Excel)
            writer.write("ID;Nom;Description;Prix (DH);Catégorie;Fournisseur\n");
            
            for (Produit p : produits) {
                // Échapper les guillemets et entourer chaque champ de guillemets
                String id = escapeCSV(p.getId() != null ? p.getId().toString() : "");
                String nom = escapeCSV(p.getNom() != null ? p.getNom() : "");
                String description = escapeCSV(p.getDescription() != null ? p.getDescription() : "");
                String prix = String.format("%.2f", p.getPrixUnitaire() != null ? p.getPrixUnitaire() : 0.0);
                String categorie = escapeCSV(p.getCategorie() != null ? p.getCategorie().getNom() : "");
                String fournisseur = escapeCSV(p.getFournisseur() != null ? p.getFournisseur().getNom() : "");
                
                writer.write(String.format("%s;%s;%s;%s;%s;%s\n",
                        id, nom, description, prix, categorie, fournisseur));
            }
        }
    }
    
    private static String escapeCSV(String value) {
        if (value == null) return "\"\"";
        // Remplacer les guillemets par des guillemets doubles et entourer de guillemets
        if (value.contains("\"") || value.contains(";") || value.contains("\n") || value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return "\"" + value + "\"";
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
