package com.java.gestion_stock.controller;

import com.java.gestion_stock.entity.Stock;
import com.java.gestion_stock.service.StockService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockservice;

    public StockController(StockService stockservice) {
        this.stockservice = stockservice;
    }

    @GetMapping
    public List<Stock> getAllStock() {
        return stockservice.getStock();
    }

    @GetMapping("/{produitId}")
    public Stock getStockByProduit(@PathVariable UUID produitId) {
        return stockservice.getStockByIdProduit(produitId);
    }

    @PostMapping
    public Stock addStock(@RequestBody Stock stock) {
        return stockservice.saveStock(stock);
    }

    @DeleteMapping("/{stockId}")

    public void deleteStock(@PathVariable UUID stockId) {
        stockservice.deleteStock(stockId);
    }
}
