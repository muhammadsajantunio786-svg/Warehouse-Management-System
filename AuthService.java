package service;

import exception.AuthenticationException;
import model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles user authentication and session management.
 */
public class AuthService {
    private final List<User> users = new ArrayList<>();
    private User currentUser;

    public AuthService() {
        // Seed default users
        users.add(new User(1, "admin",
                User.hashPassword("admin123"), "Admin Manager", User.Role.MANAGER));
        users.add(new User(2, "worker",
                User.hashPassword("worker123"), "Warehouse Worker", User.Role.WORKER));
    }

    public User login(String username, String password) throws AuthenticationException {
        User found = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("User '" + username + "' not found."));

        if (!found.checkPassword(password)) {
            throw new AuthenticationException("Incorrect password.");
        }
        currentUser = found;
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isManager() {
        return currentUser != null && currentUser.isManager();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
