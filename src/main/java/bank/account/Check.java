package bank.account;
import bank.user.Customer;

public class Check extends Account {

    public Check(String accountNumber, double balance, String status, Customer customer) {
        super(accountNumber, "Check", balance, status, customer);
    }

    public Check(String accountNumber, double balance, String status) {
        super(accountNumber, "Check", balance, status);
    }

    public void title(){
        System.out.println("**Check Title**");
    }

    @Override
    public void pay() {
        //check title
        title();
        System.out.println("Check payment for customer: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Check receipt for customer: " + customer.getName());    
    }
}

