package bank.user;


public class Teller extends User  {
    public Teller(String username, String password, String emailString) {
        super(username, password, emailString, Role.TELLER);
    }
}
