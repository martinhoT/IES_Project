package com.getaroom.app.other;

public class LoginData {
    private final String login;
    private final String password;
    private final String role;

    public LoginData(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = "analyst";
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
