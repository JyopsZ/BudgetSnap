package com.example.budgetsnap;

public class AccountList {
    private String Name;
    private String Category;
    private String Amount;
    private String Date;

    public AccountList(String Name, String Category, String Amount, String Date) {
        this.Name = Name;
        this.Category = Category;
        this.Amount = Amount;
        this.Date = Date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }
}
