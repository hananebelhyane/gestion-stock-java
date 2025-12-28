package gestiondestock.model;

public class AuthResponse {
    private String token;
    private String role;
    private String username;
    private String userId;

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }
}
