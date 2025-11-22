package account;

import java.util.List;

import user.Customer;

import java.util.ArrayList;

public class Card extends Account {
    private List<String> transactions = new ArrayList<>();

    public Card(Customer customer) {
        super(customer);
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

