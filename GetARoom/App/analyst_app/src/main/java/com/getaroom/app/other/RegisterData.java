package com.getaroom.app.other;

public class RegisterData {
    private final String username;
    private final String password;
    private final String email;
    private final String role;

    public RegisterData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = "analyst";
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    
}
