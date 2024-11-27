package com.example.budgetsnap;

public class BudgetModel {
    private String category;
    private double expenses;
    private double remaining;

    public BudgetModel(String category, double expenses, double remaining) {
        this.category = category;
        this.expenses = expenses;
        this.remaining = remaining;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }
}
