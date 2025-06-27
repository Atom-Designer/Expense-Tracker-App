package com.example.expensetracker;

import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String type;
    private String category;
    private String description;
    private double amount;

    public Transaction(LocalDate date, String type, String category, String description, double amount) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + date + "] " + type + " - " + category + ": $" + amount + " (" + description + ")";
    }

}
