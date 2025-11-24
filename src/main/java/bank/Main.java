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
import bank.user.UserDetails;
import bank.user.repository.InMemoryUserRepository;
import javafx.application.Application;
import java.util.List;

import java.util.List;

import bank.user.Administrator;

public class Main {
    public static void main(String[] args) {

        // Initialize UserManager
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        UserManager userManager = new UserManager(userRepository);

        try {
            // Admin creating a Teller and a Customer
            UserDetails adminDetails = new UserDetails("adminUser", "adminPass", "admin@example.com", "Admin Name", 35, "123456789");
            User admin = userManager.createUser(adminDetails, Role.ADMIN, Role.ADMIN);
            System.out.println("Created admin: " + admin.getUsername() + " with role " + admin.getRole());

            UserDetails tellerDetails = new UserDetails("tellerUser", "tellerPass", "teller@example.com", "Teller Name", 30, "987654321");
            User teller = userManager.createUser(tellerDetails, Role.TELLER, admin.getRole());
            System.out.println("Created teller: " + teller.getUsername() + " with role " + teller.getRole());

            UserDetails customerDetails = new UserDetails("customerUser", "customerPass", "customer@example.com", "Shayan Aminaei", 10, "100");
            Customer customer = (Customer) userManager.createUser(customerDetails, Role.CUSTOMER, teller.getRole());
            System.out.println("Created customer: " + customer.getUsername() + " with role " + customer.getRole());

            // Generic user
            UserDetails genDetails = new UserDetails("genericUser", "genPass", "generic@example.com", "Generic Name", 25, "555555555");
            User genUser = userManager.createUser(genDetails, Role.CUSTOMER, admin.getRole());
            System.out.println("Created generic user: " + genUser.getUsername() + " with role " + genUser.getRole());

            // Assign roles
            userManager.assignRole(genUser.getId(), Role.TELLER, admin.getRole());
            System.out.println("Successfully assign role TELLER to " + genUser.getUsername());

            // Attempt invalid role assignment (should throw exception)
            try {
                userManager.assignRole(genUser.getId(), Role.CUSTOMER, teller.getRole());
            } catch (Exception e) {
                System.out.println("Failed to assign role by Teller: " + e.getMessage());
            }

            // Print all users
            System.out.println("\nAll users in system:");
            List<User> allUsers = userManager.getUsers();
            for (User u : allUsers) {
                System.out.println("User: " + u.getUsername() + ", Role: " + u.getRole());
            }

            // New customer
            customer.printCustomerInfo();
            System.out.println();

            // Customer testing
            Customer johnDeer = new Customer("John_Deer", "password123", "John_Deer@gmail.com", "John Deer", 10, "(123) 456-7891");
            Administrator johnAdmin = new Administrator("John", "password456", "admin@gmail.com");

            // Making different accounts
            Card card = new Card("123", 0, "Status", johnDeer);
            Check check = new Check("456", 0, "Status", johnDeer);
            Saving saving = new Saving("678", 0, "Status", johnDeer);

            // Transations for each account
            Transaction t1 = new Transaction("type", "status", 100, "recipient", card);
            Transaction t2 = new Transaction("type", "status", 100, "recipient", check);
            Transaction t3 = new Transaction("type", "status", 100, "recipient", saving);

            card.addTransaction(t1);
            check.addTransaction(t2);
            saving.addTransaction(t3);

            johnDeer.addAccount(card);
            johnDeer.addAccount(check);

            // Deposite/Withdraw testing
            card.deposit(1000);
            check.deposit(2000);
            
            johnDeer.printCustomerInfo();
            
            // Admin testing
            johnAdmin.viewBalance(saving);
            johnAdmin.deposit(card, 99);
            johnDeer.printCustomerInfo(); // Print info after the admin depositted 99$

            // Look at the transactions in card
            System.out.println(card.getTransactions());
            
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
