package bank.user;
import java.util.ArrayList;
import java.util.List;

import bank.account.Account;
import bank.utils.InternalLogger;

public class Teller extends User {
    
    private final InternalLogger logger = new InternalLogger();

    public Teller(UserDetails userDetails) {
        super(userDetails, Role.TELLER);
    }

    public double viewBalance(Account account) {
        logger.info(this.getRole() + " (username: " + getUsername() + ", id: " + getId() + ") is viewing balance for account " + account.getAccountNumber());
        return account.getBalance();
    }

    public void deposit(Account account, double amount) {
        logger.info(this.getRole() + " (username: " + getUsername() + ", id: " + getId() + ") deposited " + amount + " into account " + account.getAccountNumber());
        account.deposit(amount);
    }

    public void withdraw(Account account, double amount) {
        logger.info(this.getRole() + " (username: " + getUsername() + ", id: " + getId() + ") withdrew " + amount + " from account " + account.getAccountNumber());
        account.withdraw(amount);
    }

    public void openNewAccount(long customerId, Account account) {
        Users.load();

        List<Users.User> users = Users.get();
        Users.User oldUser = null;

        // Find the user
        for (Users.User u : users) {
            if (u.id() == customerId) {
                oldUser = u;
                break;
            }
        }

        if (oldUser == null) {
            throw new IllegalArgumentException("User with ID " + customerId + " not found");
        }

        // Capture name for lambda (must be effectively final)
        final String customerName = oldUser.name();
        final String accountNum = account.getAccountNumber();

        // Convert transactions from Account to Users.Transaction (if any exist)
        List<Users.Transaction> txList = new ArrayList<>();
        if (account.getTransactions() != null && !account.getTransactions().isEmpty()) {
            txList = account.getTransactions().stream()
                    .map(t -> new Users.Transaction(
                            t.getDate().getTime(),          // convert Date to long
                            t.getAmount(),                  // amount is now double in Users.Transaction
                            t.getType(),                    // use type as details
                            "0",                            // to_account (String, "0" for no destination)
                            accountNum,                     // from_account (use account parameter)
                            customerId,                     // recipient_id (use customerId parameter)
                            customerName                    // recipient_name (use customer's name)
                    ))
                    .toList();
        }


        // Convert Account to Users.Account record
        Users.Account newAccount = new Users.Account(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                txList
        );

        // Copy the old accounts and add the new one
        List<Users.Account> updatedAccounts = new ArrayList<>(oldUser.accounts());
        updatedAccounts.add(newAccount);

        // Create a new User record with the updated accounts
        Users.User updatedUser = new Users.User(
                oldUser.id(),
                oldUser.username(),
                oldUser.name(),
                oldUser.role(),
                oldUser.password(),
                oldUser.email(),
                updatedAccounts
        );

        // Remove the old user and add the updated one
        users.remove(oldUser);
        users.add(updatedUser);

        Users.save();
    }

    public void closeAccount(long userId, String accountNumber) {
        Users.load();
        List<Users.User> users = Users.get();
        Users.User oldUser = null;

        // Find the user
        for (Users.User u : users) {
            if (u.id() == userId) {
                oldUser = u;
                break;
            }
        }

        if (oldUser == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        // Create new list of accounts WITHOUT the account to close
        List<Users.Account> updatedAccounts = new ArrayList<>();
        for (Users.Account acc : oldUser.accounts()) {
            if (!acc.number().equals(accountNumber)) {
                updatedAccounts.add(acc);
            }
        }

        // If no accounts left, remove the user entirely
        if (updatedAccounts.isEmpty()) {
            users.remove(oldUser);
            logger.info("User " + userId + " removed (no accounts remaining)");
        } else {
            // Create new User record with updated accounts
            Users.User updatedUser = new Users.User(
                    oldUser.id(),
                    oldUser.username(),
                    oldUser.name(),
                    oldUser.role(),
                    oldUser.password(),
                    oldUser.email(),
                    updatedAccounts
            );

            // Replace old user with updated user
            users.remove(oldUser);
            users.add(updatedUser);
            logger.info("Account " + accountNumber + " closed for user " + userId);
        }

        Users.save();
    }
}
