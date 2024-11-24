package com.example.budgetsnap;

public class UserClass {

    private String Unum;
    private String name;
    private String birthday;
    private String email;
    private String password;
    private double income;
    private double expense;


    public UserClass(String Unum, String name, String birthday, String email, String password, double income, double expense) {

        this.Unum = Unum;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
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
}
