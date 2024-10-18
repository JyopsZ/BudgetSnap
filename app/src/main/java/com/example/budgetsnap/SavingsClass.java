package com.example.budgetsnap;

public class SavingsClass {

    private String name;
    private double goalAmount;
    private String frequency;
    private String dateFinish;
    private double currentAmount;
    private boolean isActivated;

    public SavingsClass(String name, double goalAmount, String frequency, String dateFinish, double currentAmount, boolean isActivated) {

        this.name = name;
        this.goalAmount = goalAmount;
        this.frequency = frequency;
        this.dateFinish = dateFinish;
        this.currentAmount = currentAmount;
        this.isActivated = isActivated;
    }

    // GETTERS
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

    public boolean getIsActivated() {

        return isActivated;
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

        this.isActivated = isActivated;
    }
}
