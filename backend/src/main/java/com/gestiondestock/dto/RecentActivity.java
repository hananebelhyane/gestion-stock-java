package com.gestiondestock.dto;

import java.time.LocalDateTime;

public class RecentActivity {
    public String product;
    public int quantity; // positive for entry, negative for removal
    public LocalDateTime date;
    public String type; // "ENTRY" or "REMOVAL"

    public RecentActivity() {}

    public RecentActivity(String product, int quantity, LocalDateTime date, String type) {
        this.product = product;
        this.quantity = quantity;
        this.date = date;
        this.type = type;
    }
}