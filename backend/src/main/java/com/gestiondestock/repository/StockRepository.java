package com.gestiondestock.repository;
import com.gestiondestock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {

        // les produits en repture de stock
    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible=0")
    List<Stock> findProduitsEnRupture();

        //les produits avec un stock faible
    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte AND s.quantiteDisponible >0")
    List<Stock> findProduitsStockFaible();

        //le nombre des produits en reptures
    @Query("SELECT COUNT(s) FROM Stock s  WHERE s.quantiteDisponible=0")
    long countProduitsEnRupture();

        //le nombre des produits faibles
    @Query("SELECT COUNT(s) FROM Stock s  WHERE s.quantiteDisponible <= s.seuilAlerte AND s.quantiteDisponible >0")
    long countProduitsFaible();


        //trouvant le stock d un produit
        Stock findByProduitId(UUID produitId);

    //findBy :: indique une recherche
        // Produit_ProduitId : indique le chemin d’accès à un attribut d’une autre entité
        //ci, Stock a un champ produit de type Produit, qui est lié à une autre table.
        //Et dans Produit, on a :private Integer produitId
        //Produit_ProduitId==stock.getProduit().getProduitId()
        //Et il te retourne le Stock correspondant à ce produit.





}
