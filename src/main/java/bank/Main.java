package bank;

import bank.account.Card;
import bank.account.Check;
import bank.account.Saving;
import bank.branch.Bank;
import bank.branch.Branch;
import bank.transaction.Transaction;
import bank.user.Customer;
import bank.gui.GUI;
import bank.user.UserManager;
import bank.user.Role;
import bank.user.User;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {

        // Initialize UserManager
        UserManager userManager = new UserManager();

        try {
            // Admin creating a Teller and a Customer
            User admin = userManager.createUser("adminUser", "adminPass", "admin@example.com", "Admin Name", 35, "123456789", Role.ADMIN, Role.ADMIN);
            User teller = userManager.createUser("tellerUser", "tellerPass", "teller@example.com", "Teller Name", 30, "987654321", Role.TELLER, Role.ADMIN);
            Customer customer = (Customer) userManager.createUser("customerUser", "customerPass", "customer@example.com", "Shayan Aminaei", 10, "100", Role.CUSTOMER, Role.TELLER);
            User genUser = userManager.createUser("genericUser", "genPass", "generic@example.com", "Generic Name", 25, "555555555", Role.CUSTOMER, admin.getRole());
            userManager.assignRole(genUser.getId(), Role.TELLER, admin.getRole());
            userManager.assignRole(genUser.getId(), Role.CUSTOMER, teller.getRole()); // Should throw an exception

            // New customer
            customer.printCustomerInfo();
            System.out.println();

            // Making different accounts
            Card card = new Card(customer);
            Check check = new Check(customer);
            Saving saving = new Saving(customer);

            // Transations for each account
            Transaction t1 = new Transaction("type", "status", 100, "recipient", card);
            Transaction t2 = new Transaction("type", "status", 100, "recipient", check);
            Transaction t3 = new Transaction("type", "status", 100, "recipient", saving);

            card.addTransaction(t1);
            check.addTransaction(t2);
            saving.addTransaction(t3);

            // Transactions
            card.pay();
            card.receipt();
            System.out.println();

            check.pay();
            check.receipt();
            System.out.println();

            saving.pay();
            saving.receipt();
            System.out.println();

            // Bank and branches Test
            Bank bank = new Bank("National Bank");
            Branch branch1 = new Branch("Branch no1 ", bank);
            Branch branch2 = new Branch("Branch no2 ", bank);

            bank.printBankInfo();
            System.out.println();

            // Transaction's test
            System.out.println("Card   transactions count:   " + card.getTransactions().size());
            System.out.println("Check  transactions count:   " + check.getTransactions().size());
            System.out.println("Saving transactions count:   " + saving.getTransactions().size());


        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        Application.launch(GUI.class, args);

    }

}
