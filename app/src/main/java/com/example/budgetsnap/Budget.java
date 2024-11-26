package com.example.budgetsnap;

public class Budget {
    private String title;
    private Double remaining;
    private Double expenses;
    private String category;

    public Budget(String category, Double remaining, Double expenses) {
        this.category = category;
        this.remaining = remaining;
        this.expenses = expenses;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Double getRemaining() {
        return remaining;
    }

    public Double getExpenses() {
        return expenses;
    }

    // Utility Method to Calculate Total Budget
    public Double getTotalBudget() {
        return remaining + expenses;
    }

    // Setters (if needed)
    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", remaining=" + remaining +
                ", expenses=" + expenses +
                '}';
    }
}
