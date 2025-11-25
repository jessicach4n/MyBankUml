package bank.user;


public class Teller extends User  {
    public Teller(UserDetails userDetails) {
        super(userDetails, Role.TELLER);
    }
}
