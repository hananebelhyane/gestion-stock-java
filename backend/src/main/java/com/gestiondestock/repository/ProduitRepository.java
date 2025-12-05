package com.gestiondestock.repository;

import com.gestiondestock.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProduitRepository extends JpaRepository<Produit,UUID> {

    //nombre total de produits dans le stock

    long count();

    // trouver les produits par categorie


    List<Produit> findByCategorieId(UUID categorieId);

    // Explication:
    // - findBy : cherche
    // - Categorie_ : dans l'attribut "categorie" de Produit
    // - CategorieId : l'attribut "categorieId" de Categorie
    // SQL: SELECT p.* FROM PRODUIT p
    //      JOIN CATEGORIE c ON p.categorie_id = c.categorie_id
    //      WHERE c.categorie_id = ?

    //la valeur totale de stock:la somme totale de la valeur de tous les produits actuellement en stock
    //sum:somme des resultat

    @Query("SELECT SUM(s.produit.prixUnitaire * s.quantiteDisponible) FROM Stock s")
    // Query : Définit une requête personnalisée en JPQL
    //on peut faire @Query("SELECT SUM(s.produit.prixUnitaire * s.quantiteDisponible) FROM Stock s")

    Double calculerValeurTotaleStock();
    //quand tu appelles la méthode, Spring :
    //
    //exécute la requête définie dans @Query;
    //
    //récupère la valeur calculée (la somme);
    //
    //te la retourne sous forme d’un BigDecimal.


}
