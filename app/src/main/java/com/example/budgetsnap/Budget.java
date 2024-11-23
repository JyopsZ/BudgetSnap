package com.example.budgetsnap;


public class Budget {
    private String title;
    private Double remaining;
    private Double expenses;
    private String category;


    public Budget(String category, String title, Double remaining, Double expenses) {
        this.category = category;
        this.title = title;
        this.remaining = remaining;
        this.expenses = expenses;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Double getRemaining() {return remaining;}


    public Double getExpenses() {return expenses;}
}
