package bank.user;
import bank.account.Account;
import bank.utils.InternalLogger;

public class Administrator extends User {
    public Administrator(UserDetails userDetails) {
        super(userDetails, Role.ADMIN);
    }

    // Administrator only methods (idk what exactly an admin should do so I'll just leave this for later)
}
