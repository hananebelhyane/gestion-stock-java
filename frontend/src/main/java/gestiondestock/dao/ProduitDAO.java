package gestiondestock.dao;

import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    private static final List<Produit> PRODUITS = new ArrayList<>();
    private static int nextId = 1;
    
    static {
        // Données de test
        Categorie categorie1 = new Categorie("Électronique", "Produits électroniques");
        Categorie categorie2 = new Categorie("Mobilier", "Meubles et mobilier de bureau");
        
        Fournisseur fournisseur1 = new Fournisseur("TechCorp", "tech@corp.com", "123456789", "123 Tech Street");
        Fournisseur fournisseur2 = new Fournisseur("OfficePlus", "contact@officeplus.com", "987654321", "456 Office Ave");
        
        Produit produit1 = new Produit("Ordinateur Portable", "PC portable gaming haute performance", 1200.0, "Gaming", categorie1, fournisseur1);
        produit1.setId(String.valueOf(nextId++));
        
        Produit produit2 = new Produit("Souris Gaming", "Souris gaming RGB 6400DPI", 50.0, "Précise", categorie1, fournisseur1);
        produit2.setId(String.valueOf(nextId++));
        
        Produit produit3 = new Produit("Chaise Bureau", "Chaise ergonomique réglable", 250.0, "Confortable", categorie2, fournisseur2);
        produit3.setId(String.valueOf(nextId++));
        
        PRODUITS.add(produit1);
        PRODUITS.add(produit2);
        PRODUITS.add(produit3);
    }

    public static List<Produit> getAll() {
        return new ArrayList<>(PRODUITS);
    }

    public static void save(Produit produit) {
        if (produit.getId() == null || produit.getId().isEmpty()) {
            produit.setId(String.valueOf(nextId++));
        }
        PRODUITS.add(produit);
        System.out.println("Produit sauvegardé: " + produit.getNom());
    }

    public static void update(Produit produit) {
        for (int i = 0; i < PRODUITS.size(); i++) {
            if (PRODUITS.get(i).getId().equals(produit.getId())) {
                PRODUITS.set(i, produit);
                System.out.println("Produit mis à jour: " + produit.getNom());
                return;
            }
        }
    }

    public static void delete(String id) {
        PRODUITS.removeIf(p -> p.getId().equals(id));
        System.out.println("Produit supprimé, ID: " + id);
    }

    public static Produit getById(String id) {
        return PRODUITS.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static void exportToCSV(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Nom,Description,Prix,Categorie,Fournisseur\n");
            List<Produit> produitsList = getAll();
            for (Produit p : produitsList) {
                writer.write(String.format("%s,%s,%.2f,%s,%s\n",
                    p.getNom(),
                    p.getDescription(),
                    p.getPrixUnitaire(),
                    p.getCategorie() != null ? p.getCategorie().getNom() : "",
                    p.getFournisseur() != null ? p.getFournisseur().getNom() : ""
                ));
            }
            System.out.println("Export CSV réussi: " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV: " + e.getMessage());
            Thread.dumpStack();
        }
    }
}