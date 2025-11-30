package bank.account;
import bank.user.Customer;

public class Card extends Account {

    public Card(String accountNumber, double balance, String status, Customer customer) {
        super(accountNumber, "Card", balance, status, customer);
    }

    public Card(String accountNumber, double balance, String status) {
        super(accountNumber, "Card", balance, status);
    }

    @Override
    public void pay() {
        System.out.println("Card payment for: " + customer.getName());
    }

    @Override
    public void receipt() {
        System.out.println("Card receipt for: " + customer.getName());
    }
}

