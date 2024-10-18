package com.example.budgetsnap;

public class Reminder {
    private String date;
    private String title;
    private String message;

    public Reminder(String date, String title, String message) {
        this.date = date;
        this.title = title;
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
