package bank;

import java.util.List;
import java.util.ArrayList;

public class Saving extends Account {
    private List<String> transactions = new ArrayList<>();

    public Saving(Customer customer) {
        super(customer);
    }
    public void title(){
        System.out.println("**Payments**");
    };

    public List<String> getTransactions() {
        return transactions;
    }

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
