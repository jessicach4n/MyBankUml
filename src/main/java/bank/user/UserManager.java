package bank.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private final List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    // Validate if the current role can create a user of the target role
    public boolean validateUserCreationRights(Role currentUserRole, Role targetRole) {
        return switch (currentUserRole) {
            case Role.ADMIN -> targetRole == Role.CUSTOMER || targetRole == Role.TELLER || targetRole == Role.ADMIN;
            case Role.TELLER -> targetRole == Role.CUSTOMER; // Only Teller can create customers
            default -> false; // Customers cannot create users
        };
    }

    // Create new user
    public User createUser(String username, String password, String email, String name, int age, String tel, Role targetRole, Role currentUserRole) throws Exception {
        if (!validateUserCreationRights(currentUserRole, targetRole)) {
            throw new Exception("Current role not allowed to create this user.");
        }

        // Depending on role, instantiate the right subclass
        User newUser;
        switch (targetRole) {
            case CUSTOMER -> newUser = new Customer(username, password, email, name, age, tel);
            case TELLER -> newUser = new Teller(username, password, email);
            case ADMIN -> newUser = new Administrator(username, password, email);
            default -> throw new Exception("Unknown role: " + targetRole);
        }

        newUser.setRole(targetRole);
        users.add(newUser);
        return newUser;
    }

    // Update an existing user's info
    public boolean updateUsername(long userId, String newUsername, String newPassword, String newEmail) {
        Optional<User> userOpt = users.stream().filter(u -> u.getId() == userId).findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUsername(newUsername);
            user.setPassword(newPassword);
            user.setEmailString(newEmail);
            return true;
        }
        return false;
    }

    // Assign a role to an existing user (Admin only)
    public void assignRole(long userId, Role newRole, Role currentUserRole) throws Exception {
        if (currentUserRole != Role.ADMIN) {
            throw new Exception("Only administrators can assign roles.");
        }
        Optional<User> userOpt = users.stream().filter(u -> u.getId() == userId).findFirst();
        userOpt.ifPresent(user -> user.setRole(newRole));
    }

    public List<User> getUsers() {
        return users;
    }

    public User findUserById(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

}
