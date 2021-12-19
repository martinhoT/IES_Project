package com.getaroom.app.entity;

import javax.validation.constraints.NotBlank;


public class User {
    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "password is mandatory")
    private String password;

    public User() {}

    public User(String name, String password) {
        this.name = name;
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

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", password=" + password + '}';
    }
}