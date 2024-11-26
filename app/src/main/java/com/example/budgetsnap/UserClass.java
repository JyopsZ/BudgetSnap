package com.example.budgetsnap;

public class UserClass {

    private String Unum;
    private String name;
    private String password;
    private String birthday;
    private String email;
    private byte[] image;
    private double income;
    private double expense;


    public UserClass(String Unum, String name, String password, String birthday, String email, byte[] image, double income, double expense) {

        this.Unum = Unum;
        this.name = name;
        this.password = password;
        this.birthday = birthday;
        this.email = email;
        this.image = image;
        this.income = income;
        this.expense = expense;
    }

    public String getUNum() {
        return Unum;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getIncome() {
        return income;
    }

    public double getExpense() {
        return expense;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
