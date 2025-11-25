package bank.account;
import bank.user.Customer;

public class Saving extends Account {

    public Saving(String accountNumber, double balance, String status, Customer customer) {
        super(accountNumber, "Saving", balance, status, customer);
    }
    
    public void title(){
        System.out.println("**Payments**");
    };

    @Override
    public void pay() {
        title();
        System.out.println("Payment From saving account For: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Payment receipt from saving account for: " + customer.getName());
    }
}
