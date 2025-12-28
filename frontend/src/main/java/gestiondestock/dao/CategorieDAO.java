package gestiondestock.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gestiondestock.model.Categorie;
import gestiondestock.model.CategorieDTO;
import gestiondestock.model.Session;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {
    private static final String BASE_URL = "http://localhost:8080/api/categories";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static List<Categorie> getAll() throws IOException, InterruptedException {
        HttpRequest req = baseRequest(BASE_URL)
                .GET()
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode());

        Type listType = new TypeToken<ArrayList<CategorieDTO>>() {
        }.getType();
        List<CategorieDTO> dtos = GSON.fromJson(resp.body(), listType);
        List<Categorie> categories = new ArrayList<>();
        for (CategorieDTO dto : dtos) {
            Categorie cat = new Categorie();
            cat.setId(dto.getId());
            cat.setNom(dto.getNom());
            cat.setDescription(dto.getDescription());
            categories.add(cat);
        }
        return categories;
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
