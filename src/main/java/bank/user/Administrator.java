package bank.user;
import bank.account.Account;
import bank.utils.InternalLogger;

public class Administrator extends Teller {
    public Administrator(UserDetails userDetails) {
        super(userDetails);
        super.setRole(Role.ADMIN);
    }
}
