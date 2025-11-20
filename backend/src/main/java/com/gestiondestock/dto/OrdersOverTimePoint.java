package com.gestiondestock.dto;

import java.time.LocalDate;

public class OrdersOverTimePoint {
    public LocalDate date;
    public int count;

    public OrdersOverTimePoint() {}

    public OrdersOverTimePoint(LocalDate date, int count) {
        this.date = date;
        this.count = count;
    }
}