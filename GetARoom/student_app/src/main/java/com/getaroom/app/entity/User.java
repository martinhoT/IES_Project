package com.getaroom.app.entity;

import javax.validation.constraints.NotBlank;


public class User {
    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "Email address is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;


    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setpassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getpassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", password=" + password + '}';
    }
}