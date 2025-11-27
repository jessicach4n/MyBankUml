package bank.user;

import bank.user.repository.UserRepository;

import java.util.Optional;

/**
 * Authentication service that validates user credentials.
 * Supports authentication by both username and user ID.
 */
public class Authentication {
    private final UserRepository repository;

    public Authentication(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Authenticate a user by username and password.
     *
     * @param username the username to authenticate
     * @param password the password to verify
     * @return Optional containing the authenticated User if successful, empty otherwise
     */
    public Optional<Users.User> authenticateByUsername(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            System.out.println("[AUTH] Username or password is null/empty");
            return Optional.empty();
        }

        System.out.println("[AUTH] Attempting login for username: " + username);
        System.out.println("[AUTH] Total users in repository: " + repository.getAll().size());

        // Debug: print all usernames
        repository.getAll().forEach(u ->
            System.out.println("[AUTH] Available user: " + u.username() + " (id: " + u.id() + ")")
        );

        // Search for user by username
        Users.User user = repository.getAll().stream()
                .filter(u -> username.equals(u.username()))
                .findFirst()
                .orElse(null);

        if (user == null) {
            System.out.println("[AUTH] User not found: " + username);
            return Optional.empty();
        }

        System.out.println("[AUTH] User found: " + user.username());
        System.out.println("[AUTH] Stored password: " + user.password());
        System.out.println("[AUTH] Provided password: " + password);

        // Verify password
        if (!password.equals(user.password())) {
            System.out.println("[AUTH] Password mismatch!");
            return Optional.empty();
        }

        System.out.println("[AUTH] Authentication successful!");
        return Optional.of(user);
    }

    /**
     * Authenticate a user by user ID and password.
     *
     * @param userId the user ID to authenticate
     * @param password the password to verify
     * @return Optional containing the authenticated User if successful, empty otherwise
     */
    public Optional<Users.User> authenticateById(long userId, String password) {
        if (password == null) {
            return Optional.empty();
        }

        Users.User user = repository.getById(userId);
        if (user == null) {
            return Optional.empty();
        }

        // Verify password
        if (!password.equals(user.password())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    /**
     * Change a user's password.
     *
     * @param userId the user ID
     * @param currentPassword the current password (for verification)
     * @param newPassword the new password to set
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(long userId, String currentPassword, String newPassword) {
        // Verify current password
        Optional<Users.User> authResult = authenticateById(userId, currentPassword);
        if (authResult.isEmpty()) {
            return false;
        }

        Users.User user = authResult.get();

        // Create updated user with new password
        Users.User updatedUser = new Users.User(
            user.id(),
            user.username(),
            user.name(),
            user.role(),
            newPassword,  // new password
            user.email(),
            user.accounts()
        );

        repository.update(updatedUser);
        repository.save();
        return true;
    }

    /**
     * Verify if a user exists by username.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return repository.getAll().stream()
                .anyMatch(u -> username.equals(u.username()));
    }
}
