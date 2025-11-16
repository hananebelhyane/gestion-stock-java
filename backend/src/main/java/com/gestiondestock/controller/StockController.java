package com.gestiondestock.controller;

import com.gestiondestock.entity.Stock;
import com.gestiondestock.service.StockService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockservice;

    public StockController(StockService stockservice) {
        this.stockservice = stockservice;
    }

    private void checkRole(String role) {
        if (role == null || !(role.equals("ADMIN") || role.equals("MAGASINIER"))) {
            throw new RuntimeException("ACCES_REFUSE");
        }
    }

    @GetMapping
    public List<Stock> getAllStock(@RequestHeader("role") String role) {
        checkRole(role);
        return stockservice.getStock();
    }

    @GetMapping("/{produitId}")
    public Stock getStockByProduit(
            @PathVariable UUID produitId,
            @RequestHeader("role") String role) {
        checkRole(role);
        return stockservice.getStockByIdProduit(produitId);
    }

    @PostMapping
    public Stock addStock(
            @RequestBody Stock stock,
            @RequestHeader("role") String role) {
        checkRole(role);
        return stockservice.saveStock(stock);
    }

    @DeleteMapping("/{stockId}")
    public void deleteStock(
            @PathVariable UUID stockId,
            @RequestHeader("role") String role) {
        checkRole(role);
        stockservice.deleteStock(stockId);
    }

    @PutMapping("/{stockId}")
    public Stock updateStock(@PathVariable UUID stockId, @RequestBody Stock stock, @RequestHeader("role") String role) {
        checkRole(role);
        return stockservice.updateStock(stockId, stock);
    }

}