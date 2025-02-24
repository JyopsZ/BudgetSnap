package com.example.budgetsnap;

public class Transaction {
    private String name;
    private String category;
    private String amount;
    private String date;
    private boolean isPositive;
    private byte[] image; // Binary data for the image
    private String tNum;

    public Transaction(String tNum, String name, String date, String amount, boolean isPositive, String category, byte[] image) {
        this.tNum = tNum;
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.isPositive = isPositive;
        this.category = category;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public boolean isPositive() {
        return isPositive;
    }

    public String getTNum() {
        return tNum;
    }

    public void setTNum(String tNum) {
        this.tNum = tNum;
    }
}
