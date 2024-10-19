package com.example.budgetsnap;

public class Transaction {

    private String name;
    private String category;
    private String amount;
    private boolean isPositive;

    // Constructor to initialize the Transaction object
    public Transaction(String name, String category, String amount, boolean isPositive) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.isPositive = isPositive;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isPositive() {
        return isPositive;
    }

    // Setters (if you need them)
    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }
}
