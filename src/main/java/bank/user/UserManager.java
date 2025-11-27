package bank.user;

import bank.account.Account;
import bank.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserManager {
    private final UserRepository repository;
    private final Authentication authentication;

    public UserManager(UserRepository repository) {
        this.repository = repository;
        this.authentication = new Authentication(repository);
    }

    /**
     * Authenticate a user by username and password.
     *
     * @param username the username
     * @param password the password
     * @return Optional containing the domain User if successful, empty otherwise
     */
    public Optional<User> login(String username, String password) {
        Optional<Users.User> persistenceUser = authentication.authenticateByUsername(username, password);
        return persistenceUser.map(UserManager::convertFromPersistence);
    }

    /**
     * Authenticate a user by user ID and password.
     *
     * @param userId the user ID
     * @param password the password
     * @return Optional containing the domain User if successful, empty otherwise
     */
    public Optional<User> loginById(long userId, String password) {
        Optional<Users.User> persistenceUser = authentication.authenticateById(userId, password);
        return persistenceUser.map(UserManager::convertFromPersistence);
    }

    /**
     * Change a user's password.
     *
     * @param userId the user ID
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(long userId, String currentPassword, String newPassword) {
        return authentication.changePassword(userId, currentPassword, newPassword);
    }

    /**
     * Check if a username already exists.
     *
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return authentication.userExists(username);
    }

    // Convert domain User -> persistence User
    public static Users.User convertToPersistence(User user) {
        return new Users.User(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name(),
                user.getPassword(),
                user.getEmailString(),
                new ArrayList<>() // convert domain accounts to Users.Account eventually
        );
    }

    // Convert persistence User -> domain User
    public static User convertFromPersistence(Users.User pUser) {
        Role role = Role.valueOf(pUser.role());
        User user;
        switch (role) {
            case CUSTOMER -> user = new Customer(new UserDetails(pUser.username(), pUser.password(), pUser.email(), pUser.name()), 21, "514-112-1234", null);
            case TELLER -> user = new Teller(new UserDetails(pUser.username(), pUser.password(), pUser.email(), pUser.name()));
            case MANAGER -> user = new Teller(new UserDetails(pUser.username(), pUser.password(), pUser.email(), pUser.name())); // Using Teller class temporarily
            case ADMIN -> user = new Administrator(new UserDetails(pUser.username(), pUser.password(), pUser.email(), pUser.name()));
            default -> throw new IllegalArgumentException("Unknown role: " + pUser.role());
        }
        user.setId(pUser.id());
        user.setRole(role);
        return user;
    }

    private static List<Users.Account> convertAccountsToPersistence(List<Account> domainAccounts) {
        List<Users.Account> pAccounts = new ArrayList<>();
        if (domainAccounts == null) return pAccounts;
        for (Account a : domainAccounts) {
            pAccounts.add(new Users.Account(
                    a.getAccountNumber(),
                    a.getAccountType(),
                    a.getBalance(),
                    new ArrayList<>() // optionally convert transactions
            ));
        }
        return pAccounts;
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
        User newUser = getUser(details, targetRole);

        newUser.setRole(targetRole);
        Users.User pUser = convertToPersistence(newUser);
        repository.add(pUser);
        repository.save();

        return newUser;
    }

    private static User getUser(UserDetails details, Role targetRole) throws Exception {
        User newUser;
        switch (targetRole) {
            case CUSTOMER -> newUser = new Customer( new UserDetails(details.getUsername(), details.getPassword(), details.getEmail(), details.getName()),21, "514-112-1234", null);
            case TELLER -> newUser = new Teller(new UserDetails(details.getUsername(), details.getPassword(), details.getEmail(), details.getName()));
            case ADMIN -> newUser = new Administrator(new UserDetails(details.getUsername(), details.getPassword(), details.getEmail(), details.getName()));
            default -> throw new Exception("Unknown role: " + targetRole);
        }
        return newUser;
    }

    public User updateUser(long userId, UserDetails newDetails) throws Exception {
        return updateUser(userId, newDetails, null);
    }

    // Update an existing user's info
    public User updateUser(long userId, UserDetails newDetails, ArrayList<Account> newAccounts) throws Exception {
        Users.User record = repository.getById(userId);
        if  (record == null) {
            throw new Exception("User with id " + userId + " not found.");
        }

        List<Users.Account> accountsToUse = (newAccounts != null)
                ? convertAccountsToPersistence(newAccounts)
                : record.accounts();

        // Create a new Users.User with updated fields
        Users.User updatedRecord = new Users.User(
                record.id(),
                newDetails.getUsername(),
                newDetails.getName(),
                record.role(),
                newDetails.getPassword(),
                newDetails.getEmail(),
                new ArrayList<>(accountsToUse)
        );

        repository.update(updatedRecord);

        // Convert back to domain User and return
        return convertFromPersistence(updatedRecord);
    }

    // Assign a role to an existing user (Admin only)
    public User assignRole(long userId, Role newRole, Role performedByRole) throws Exception {
        // Only admins can assign roles
        if (performedByRole != Role.ADMIN) {
            throw new Exception("Only administrators can assign roles.");
        }

        // Get the persistence user from repository
        Users.User record = repository.getById(userId);
        if (record == null) {
            throw new Exception("User with id " + userId + " not found.");
        }

        // Create a new persistence User with updated role
        Users.User updatedRecord = new Users.User(
                record.id(),
                record.username(),
                record.name(),
                newRole.name(),     // updated role
                record.password(),
                record.email(),
                new ArrayList<>(record.accounts()) // keep existing accounts
        );

        // Update the repository
        repository.update(updatedRecord);

        // Convert back to domain User and return
        return convertFromPersistence(updatedRecord);
    }


    public List<User> getUsers() {
        return repository.getAll().stream()
                .map(UserManager::convertFromPersistence)
                .toList();
    }

    public User findUserById(long id) {
        Users.User pUser = repository.getById(id);
        return pUser == null ? null : convertFromPersistence(pUser);
    }

    public Users.User addAccount(Users.User user, Users.Account account) {
        List<Users.Account> updatedAccounts = new ArrayList<>(user.accounts());
        updatedAccounts.add(account);

        return new Users.User(
                user.id(),
                user.username(),
                user.name(),
                user.role(),
                user.password(),
                user.email(),
                updatedAccounts
        );
    }

    public Users.User removeAccount(Users.User user, String accountNumber) {
        List<Users.Account> updatedAccounts = user.accounts().stream()
                .filter(a -> a.number().equals(accountNumber))
                .toList(); // Java 16+ returns an immutable list, wrap in ArrayList if needed

        return new Users.User(
                user.id(),
                user.username(),
                user.name(),
                user.role(),
                user.password(),
                user.email(),
                new ArrayList<>(updatedAccounts)
        );
    }

    public Users.User updateAccount(Users.User user, Users.Account updatedAccount) {
        List<Users.Account> updatedAccounts = user.accounts().stream()
                .map(a -> Objects.equals(a.number(), updatedAccount.number()) ? updatedAccount : a)
                .toList();

        return new Users.User(
                user.id(),
                user.username(),
                user.name(),
                user.role(),
                user.password(),
                user.email(),
                new ArrayList<>(updatedAccounts)
        );
    }

    public Users.User addTransaction(Users.User user, String accountNumber, Users.Transaction tx) {
        List<Users.Account> updatedAccounts = user.accounts().stream()
                .map(a -> {
                    if (Objects.equals(a.number(), accountNumber)) {
                        List<Users.Transaction> updatedTxs = new ArrayList<>(a.transactions());
                        updatedTxs.add(tx);
                        return new Users.Account(a.number(), a.type(), a.balance(), updatedTxs);
                    } else {
                        return a;
                    }
                })
                .toList();

        return new Users.User(
                user.id(),
                user.username(),
                user.name(),
                user.role(),
                user.password(),
                user.email(),
                new ArrayList<>(updatedAccounts)
        );
    }




}
