package com.gestiondestock.controller;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.service.StockService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public List<Stock> getAllStock() {
        return stockservice.getStock();
    }

    @GetMapping("/{produitId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock getStockByProduit(@PathVariable UUID produitId) {
        return stockservice.getStockByIdProduit(produitId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock createStock(
            @RequestParam UUID produitId,
            @RequestParam Integer quantiteDisponible,
            @RequestParam Integer seuilAlerte) {
        return stockservice.createStock(produitId, quantiteDisponible, seuilAlerte);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock addStock(@RequestBody Stock stock) {
        return stockservice.saveStock(stock);
    }

    @DeleteMapping("/{stockId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public void deleteStock(@PathVariable UUID stockId) {
        stockservice.deleteStock(stockId);
    }

    @PutMapping("/{stockId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock updateStock(@PathVariable UUID stockId, @RequestBody Stock stock) {
        return stockservice.updateStock(stockId, stock);
    }
}
