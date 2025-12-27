package gestiondestock.model;

public class Session {
    private static Session INSTANCE;
    private String token;
    private String role;
    private String username;
    private String userId;

    private Session() {
    }

    public static Session get() {
        if (INSTANCE == null)
            INSTANCE = new Session();
        return INSTANCE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void clear() {
        token = role = username = userId = null;
    }
}
