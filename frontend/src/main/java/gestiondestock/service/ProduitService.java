package gestiondestock.service;

import gestiondestock.dao.ProduitDAO;
import gestiondestock.model.Produit;

import java.io.IOException;
import java.util.List;

public class ProduitService {

    public List<Produit> getAllProduits() throws IOException, InterruptedException {
        return ProduitDAO.getAll();
    }

    public Produit addProduit(Produit produit) throws IOException, InterruptedException {
        return ProduitDAO.save(produit);
    }

    public Produit updateProduit(Produit produit) throws IOException, InterruptedException {
        return ProduitDAO.update(produit);
    }

    public void deleteProduit(String id) throws IOException, InterruptedException {
        ProduitDAO.delete(id);
    }

    public void exportProduits(String filename) throws IOException, InterruptedException {
        ProduitDAO.exportToCSV(filename);
    }
}
