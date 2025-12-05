package com.gestiondestock.DAO;

import com.gestiondestock.entity.Produit;
import com.gestiondestock.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.io.FileWriter;
import java.util.List;

public class ProduitDAO {

    public static List<Produit> getAll() {
        EntityManager em = JpaUtil.getEntityManager();
        return em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();
    }

    public static void save(Produit p) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
    }

    public static void exportToCSV(String file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Nom;Prix;Categorie;Fournisseur\n");
            for (Produit p : getAll()) {
                writer.write(p.getNom() + ";" + p.getPrixUnitaire() + ";" +
                        p.getCategorie().getNom() + ";" +
                        p.getFournisseur().getNom() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
