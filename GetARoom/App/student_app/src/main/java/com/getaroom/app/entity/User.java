package com.getaroom.app.entity;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

public class User {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private String repassword;

    private String role;

    public User() {}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.email = "";
        this.role = "";
    }

    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.email = role;
        this.role = "";
    }

    public User(String name, String email, String password, String repassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.repassword = repassword;
        this.role = "";
    }

    public User(String name, String email, String password, String repassword, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.repassword = repassword;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    @AssertTrue(message = "The password and the repeat password should match")
    public boolean isPasswordsEqual() {
        return password != null && password.equals(repassword);
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", password=" + password + ", email=" + email + ", role="+role+'}';
    }
}