package com.gestiondestock.repository;

import com.gestiondestock.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, UUID> {

        // le chiffre d affaires total
        @Query("SELECT SUM(lc.montantTotal) FROM LigneCommande lc " + // Ajout d'un espace avant JOIN
                        "JOIN lc.commande c WHERE c.statut ='confirmee'") // Assumé statut au lieu de status
        Double calculerChiffreAffairesTotal();

        // le chiffre d affaires par periode
        @Query("SELECT SUM(lc.montantTotal) FROM LigneCommande lc " + // Ajout d'un espace avant JOIN
                        "JOIN lc.commande c WHERE c.statut ='confirmee' " + // Assumé statut au lieu de status
                        "AND c.dateCommande BETWEEN :debut AND :fin")
        Double calculerChiffreAffairesTotalParPeriode(
                        @Param("debut") LocalDateTime debut,
                        @Param("fin") LocalDateTime fin);

        // Top produits les plus vendus
        @Query("SELECT lc.produit.id, lc.produit.nom, SUM(lc.quantite) as totalVendu " +
                        "FROM LigneCommande lc " +
                        "JOIN lc.commande c WHERE c.statut = 'confirmee' " +
                        "GROUP BY lc.produit.id, lc.produit.nom " +
                        "ORDER BY totalVendu DESC")
        List<Object[]> findTopProduitsVendus();
        // POURQUOI ON FAIT Object[]
        // Cette requête ne récupère pas seulement des entités Produit.
        //
        // Elle fait une agrégation avec SUM(lc.quantite).
        //
        // Elle renvoie donc un tableau de valeurs par ligne : [produitId, nom,
        // totalVendu].
        //
        // Spring Data ne peut pas automatiquement convertir ce résultat en objet
        // Produit car Produit n’a pas de champ totalVendu.

        // les lignes de commande par produit donné
        List<LigneCommande> findByProduitId(UUID produitId);

        // Lignes de commande par commande
        List<LigneCommande> findByCommandeId(UUID commandeId);

        Optional<LigneCommande> findByCommandeIdAndProduitId(UUID commandeId, UUID produitId);

        @Query("SELECT lc FROM LigneCommande lc JOIN lc.commande c " +
                        "WHERE c.statut = 'confirmee' AND c.dateCommande BETWEEN :debut AND :fin")
        List<LigneCommande> findByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
