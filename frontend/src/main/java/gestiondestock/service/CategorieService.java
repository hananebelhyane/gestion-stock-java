package gestiondestock.service;

import gestiondestock.dao.CategorieDAO;
import gestiondestock.model.Categorie;

import java.io.IOException;
import java.util.List;

public class CategorieService {
    public List<Categorie> getAllCategories() throws IOException, InterruptedException {
        return CategorieDAO.getAll();
    }
}
