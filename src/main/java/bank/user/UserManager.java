package bank.user;

import bank.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private final UserRepository repository;

    public UserManager(UserRepository repository) {
        this.repository = repository;
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
        repository.save(newUser);
        return newUser;
    }

    // Update an existing user's info
    public void updateUser(long userId, UserDetails newDetails) throws Exception {
        User user = repository.findById(userId);
        if  (user == null) {
            throw new Exception("User with id " + userId + " not found.");
        }

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

        User user = repository.findById(userId);
        if  (user == null) {
            throw new Exception("User with id " + userId + " not found.");
        }

        user.setRole(newRole);
        repository.save(user);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public User findUserById(long id) {
        return repository.findById(id);
    }

}
