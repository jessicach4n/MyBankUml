package bank.user;

public class Administrator extends User {
    public Administrator(UserDetails userDetails) {
        super(userDetails, Role.ADMIN);
    }
}
