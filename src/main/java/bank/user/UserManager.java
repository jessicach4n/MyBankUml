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
    public User createUser(UserDetails details, Role targetRole, Role currentUserRole) throws Exception {
        if (!validateUserCreationRights(currentUserRole, targetRole)) {
            throw new Exception("Current role not allowed to create this user.");
        }

        // Depending on role, instantiate the right subclass
        User newUser;
        switch (targetRole) {
            case CUSTOMER -> newUser = new Customer(
                    details.getUsername(),
                    details.getPassword(),
                    details.getEmail(),
                    details.getName(),
                    details.getAge(),
                    details.getTel()
            );
            case TELLER -> newUser = new Teller(details.getUsername(), details.getPassword(), details.getEmail());
            case ADMIN -> newUser = new Administrator(details.getUsername(), details.getPassword(), details.getEmail());
            default -> throw new Exception("Unknown role: " + targetRole);
        }

        newUser.setRole(targetRole);
        users.add(newUser);
        return newUser;
    }

    // Update an existing user's info
    public void updateUser(long userId, UserDetails newDetails) throws Exception {
        User user = users.stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new Exception("User not found"));

        user.setUsername(newDetails.getUsername());
        user.setPassword(newDetails.getPassword());
        user.setEmailString(newDetails.getEmail());
        if (user instanceof Customer customer) {
            customer.setName(newDetails.getName());
            customer.setAge(newDetails.getAge());
            customer.setTelNo(newDetails.getTel());
        }
    }

    // Assign a role to an existing user (Admin only)
    public void assignRole(long userId, Role newRole, Role performedByRole) throws Exception {
        if (performedByRole != Role.ADMIN) {
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
