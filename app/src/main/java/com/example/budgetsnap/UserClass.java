package com.example.budgetsnap;

public class UserClass {

    private String name;
    private String birthday;
    private String email;
    private String password;


    public UserClass(String name, String birthday, String email, String password) {

        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {

        return email;
    }

    public String getPassword() {

        return password;
    }
}
