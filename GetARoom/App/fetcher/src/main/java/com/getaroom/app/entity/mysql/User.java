package com.getaroom.app.entity.mysql;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "users")
public class User {

    @Column(name = "username", nullable = false)
    private String name;

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
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

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getpassword() {
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

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", password=" + password + ", email=" + email + ", role="+role+'}';
    }
}