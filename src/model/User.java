package model;

public class User {
    private String username;
    private String password;
    private String role; // "Admin" or "Cashier"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean isAdmin() {
        return "Admin".equals(role);
    }

    public boolean isCashier() {
        return "Cashier".equals(role);
    }

    @Override
    public String toString() {
        return String.format("User: %s, Role: %s", username, role);
    }
}
