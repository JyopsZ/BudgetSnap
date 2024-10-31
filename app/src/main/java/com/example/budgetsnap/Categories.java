package com.example.budgetsnap;

public class Categories {
    private String title;
    private String date;
    private String price;

    public Categories(String title, String date, String price) {
        this.title = title;
        this.date = date;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }
}
