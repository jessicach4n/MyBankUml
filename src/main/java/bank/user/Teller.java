package bank.user;
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
