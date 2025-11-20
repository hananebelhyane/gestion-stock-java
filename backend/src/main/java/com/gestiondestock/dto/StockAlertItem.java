package com.gestiondestock.dto;

import java.time.LocalDateTime;

public class StockAlertItem {
    public String product;
    public LocalDateTime date;
    public String message;

    public StockAlertItem() {}

    public StockAlertItem(String product, LocalDateTime date, String message) {
        this.product = product;
        this.date = date;
        this.message = message;
    }
}