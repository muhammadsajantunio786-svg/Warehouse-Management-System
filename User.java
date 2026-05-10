package model;

/**
 * Represents a system user with a role.
 * Demonstrates encapsulation and role-based logic.
 */
public class User extends WarehouseEntity {
    public enum Role { MANAGER, WORKER }

    private String username;
    private String passwordHash; // stored as simple hash for demo
    private String fullName;
    private Role role;

    public User(int userId, String username, String passwordHash, String fullName, Role role) {
        super(userId);
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    @Override
    public String getSummary() {
        return fullName + " [" + role.name() + "]";
    }

    public boolean checkPassword(String raw) {
        return Integer.toHexString(raw.hashCode()).equals(passwordHash);
    }

    public static String hashPassword(String raw) {
        return Integer.toHexString(raw.hashCode());
    }

    public boolean isManager() { return role == Role.MANAGER; }

    // Getters & Setters
    public String getUsername()     { return username; }
    public String getFullName()     { return fullName; }
    public Role   getRole()         { return role; }
    public void   setRole(Role role){ this.role = role; }
    public String getPasswordHash() { return passwordHash; }
}
