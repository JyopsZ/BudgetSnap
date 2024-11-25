package com.example.budgetsnap;

public class SavingsClass {

    private String Snum;
    private String name;
    private double currentAmount;
    private double goalAmount;
    private String frequency;
    private String dateFinish;
    private boolean status;
    private String Unum;

    public SavingsClass(String Snum, String name, double currentAmount, double goalAmount, String frequency, String dateFinish, boolean status, String Unum) {

        this.Snum = Snum;
        this.name = name;
        this.currentAmount = currentAmount;
        this.goalAmount = goalAmount;
        this.frequency = frequency;
        this.dateFinish = dateFinish;
        this.status = status;
        this.Unum = Unum;
    }

    // GETTERS
    public String getSNum() {

        return Snum;
    }

    public String getName() {

        return name;
    }

    public double getGoalAmount () {

        return goalAmount;
    }

    public String getFrequency () {

        return frequency;
    }

    public String getDateFinish () {

        return dateFinish;
    }

    public double getCurrentAmount() {

        return currentAmount;
    }

    public boolean getStatus() {

        return status;
    }

    public String getUNum() {
        return Unum;
    }

    // SETTERS FOR EDITING SAVINGS
    public void setName (String name) {

        this.name = name;
    }

    public void setGoalAmount (double goalAmount) {

        this.goalAmount = goalAmount;
    }

    public void setFrequency (String frequency) {

        this.frequency = frequency;
    }

    public void setDateFinish (String dateFinish) {

        this.dateFinish = dateFinish;
    }

    public void setCurrentAmount (double currentAmount) {

        this.currentAmount = currentAmount;
    }

    public void setIsActivated(boolean isActivated) {

        this.status = isActivated;
    }
}
