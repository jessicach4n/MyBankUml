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

        // Convert transactions from Account to Users.Transaction
        List<Users.Transaction> txList = account.getTransactions().stream()
                .map(t -> new Users.Transaction(
                        t.getDate().getTime(),          // convert Date to long
                        (long) t.getAmount(),           // convert double to long (matches Users.Transaction)
                        t.getType(),                    // use type as details
                        0,                              // to_account (if applicable, otherwise 0)
                        Long.parseLong(t.getAccount().getAccountNumber()), // from_account
                        t.getAccount().getCustomer().getId(),             // recipient_id
                        t.getRecipient()               // recipient_name
                ))
                .toList();


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
        var list = Users.get();

        for (var u : list) {
            if (u.id() == userId) {

                // remove the account
                u.accounts().removeIf(acc -> acc.number().equals(accountNumber));

                // delete user if no accounts left
                if (u.accounts().isEmpty()) {
                    list.remove(u);
                }

                break;
            }
        }

        Users.save();
    }
}
