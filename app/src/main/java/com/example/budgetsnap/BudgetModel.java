package com.example.budgetsnap;

public class BudgetModel {
    private String name;
    private double amount;
    private String date;
    private String category;

    public BudgetModel(String name, double amount, String date, String category) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // Getters and setters (optional)
}
