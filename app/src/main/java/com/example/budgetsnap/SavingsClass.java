package com.example.budgetsnap;

public class SavingsClass {

    private String name;
    private float goalAmount;
    private String frequency;
    private String dateFinish;
    private float currentAmount;

    public SavingsClass(String name, float goalAmount, String frequency, String dateFinish, float currentAmount) {

        this.name = name;
        this.goalAmount = goalAmount;
        this.frequency = frequency;
        this.dateFinish = dateFinish;
        this.currentAmount = currentAmount;
    }

    // GETTERS
    public String getName() {

        return name;
    }

    public float getGoalAmount () {

        return goalAmount;
    }

    public String getFrequency () {

        return frequency;
    }

    public String getDateFinish () {

        return dateFinish;
    }

    public float getCurrentAmount() {

        return currentAmount;
    }

    // SETTERS FOR EDITING SAVINGS
    public void setName (String name) {

        this.name = name;
    }

    public void setGoalAmount (float goalAmount) {

        this.goalAmount = goalAmount;
    }

    public void setFrequency (String frequency) {

        this.frequency = frequency;
    }

    public void setDateFinish (String dateFinish) {

        this.dateFinish = dateFinish;
    }

    public void setCurrentAmount (float currentAmount) {

        this.currentAmount = currentAmount;
    }
}
