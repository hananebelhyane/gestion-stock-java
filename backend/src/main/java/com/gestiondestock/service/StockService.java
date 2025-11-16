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

    // READ ALL
    public List<Stock> getStock() {
        return stockRepository.findAllWithProduit();
    }

    // READ BY PRODUIT
    public Stock getStockByIdProduit(UUID produitId) {
        return stockRepository.findByProduitId(produitId);
    }

    // CREATE avec vérification produit
    public Stock saveStock(Stock stock) {

        UUID produitId = stock.getProduit().getId();
        Produit p = produitRepository.findById(produitId).orElse(null);

        if (p == null) {
            throw new RuntimeException("PRODUIT_INEXISTANT");
        }
        stock.setProduit(p);
        return stockRepository.save(stock);
    }

    // UPDATE
    public Stock updateStock(UUID stockId, Stock updatedStock) {

        Stock existingStock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("STOCK_NOT_FOUND"));

        existingStock.setQuantiteDisponible(updatedStock.getQuantiteDisponible());
        existingStock.setSeuilAlerte(updatedStock.getSeuilAlerte());
        existingStock.setProduit(updatedStock.getProduit());

        return stockRepository.save(existingStock);
    }

    // DELETE
    public void deleteStock(UUID stockId) {
        stockRepository.deleteById(stockId);
    }
}
