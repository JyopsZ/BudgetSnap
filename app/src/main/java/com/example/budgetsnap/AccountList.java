package com.example.budgetsnap;

public class AccountList {
    private String name;
    private String category;
    private String amount;
    private String date;
 // Binary data for the image

    public AccountList(String name, String date, String amount,  String category) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.category = category;
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

    public String getDate() {
        return date;
    }

}
