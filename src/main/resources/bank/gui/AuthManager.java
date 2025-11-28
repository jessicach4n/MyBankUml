package bank.gui;

import bank.user.Authentication;
import bank.user.Users;

import java.util.Optional;

/**
 * Simple wrapper around Authentication that tracks the logged-in user.
 */
public class AuthManager {
    private final Authentication authentication = new Authentication();
    private Users.User currentUser;

    public Optional<Users.User> login(long userId, String password) {
        Optional<Users.User> found = authentication.authenticate(userId, password);
        currentUser = found.orElse(null);
        return found;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Users.User getCurrentUser() {
        return currentUser;
    }
}
