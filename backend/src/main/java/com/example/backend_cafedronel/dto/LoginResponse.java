package com.example.backend_cafedronel.dto;

public class LoginResponse {
    private final Integer userId;
    private final String userName;
    private final String email;
    private final String role;
    private final String token;

    public LoginResponse(Integer userId, String userName, String email, String role, String token) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Integer getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}
