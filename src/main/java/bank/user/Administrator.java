package bank.user;
import bank.account.Account;
import bank.utils.InternalLogger;

public class Administrator extends Teller {
    
    public Administrator(String username, String password, String emailString) {
        super(username, password, emailString);
        super.setRole(Role.ADMIN);
    }

    // Administrator only methods (idk what exactly an admin should do so I'll just leave this for later)
}
