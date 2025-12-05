package com.gestiondestock.service;

import com.gestiondestock.entity.Stock;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.repository.StockRepository;
import com.gestiondestock.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;

    public StockService(StockRepository stockRepository, ProduitRepository produitRepository) {
        this.stockRepository = stockRepository;
        this.produitRepository = produitRepository;
    }

    public List<Stock> getStock() {
        return stockRepository.findAllWithProduit();
    }

    public Stock getStockByIdProduit(UUID produitId) {
        return stockRepository.findByProduitId(produitId);
    }

    public Stock createStock(UUID produitId, Integer quantiteDisponible, Integer seuilAlerte) {

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("PRODUIT_INEXISTANT"));
        Stock stock = new Stock();
        stock.setProduit(produit);
        stock.setQuantiteDisponible(quantiteDisponible);
        stock.setSeuilAlerte(seuilAlerte);

        return stockRepository.save(stock);
    }

    public Stock saveStock(Stock stock) {

        UUID produitId = stock.getProduit().getId();
        Produit p = produitRepository.findById(produitId).orElse(null);

        if (p == null) {
            throw new RuntimeException("PRODUIT_INEXISTANT");
        }
        stock.setProduit(p);
        return stockRepository.save(stock);
    }

    public Stock updateStock(UUID stockId, Stock updatedStock) {

        Stock existingStock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("STOCK_NOT_FOUND"));

        existingStock.setQuantiteDisponible(updatedStock.getQuantiteDisponible());
        existingStock.setSeuilAlerte(updatedStock.getSeuilAlerte());
        existingStock.setProduit(updatedStock.getProduit());

        return stockRepository.save(existingStock);
    }

    public void deleteStock(UUID stockId) {
        stockRepository.deleteById(stockId);
    }
}
