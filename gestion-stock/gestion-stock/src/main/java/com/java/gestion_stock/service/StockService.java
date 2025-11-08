package com.java.gestion_stock.service;

import com.java.gestion_stock.entity.Stock;
import com.java.gestion_stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class StockService {
    private final StockRepository stockrepository;

    public StockService(StockRepository stockrepository) {
        this.stockrepository = stockrepository;
    }

    public List<Stock> getStock() {
        return stockrepository.findAllWithProduit();
    }

    public Stock getStockByIdProduit(UUID ProduitId) {
        return stockrepository.findByProduitId(ProduitId);
    }

    public Stock saveStock(Stock stock) {
        return stockrepository.save(stock);
    }

    public void deleteStock(UUID StockId) {
        stockrepository.deleteById(StockId);
    }
}