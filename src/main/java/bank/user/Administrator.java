package bank.user;

public class Administrator extends User {
    public Administrator(String username, String password, String emailString) {
        super(username, password, emailString, Role.ADMIN);
    }
}
