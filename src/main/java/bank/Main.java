package bank;

import bank.account.Card;
import bank.account.Check;
import bank.account.Saving;
import bank.branch.Bank;
import bank.branch.Branch;
import bank.branch.BranchManagement;
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
            testBranches();

            // Transaction's test
            System.out.println("Card   transactions count:   " + card.getTransactions().size());
            System.out.println("Check  transactions count:   " + check.getTransactions().size());
            System.out.println("Saving transactions count:   " + saving.getTransactions().size());

        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        Application.launch(GUI.class, args);

    }

    // *** Delete this if you want ***, this is just code to test my methods
    public static void testBranches() {
        System.out.println("============= Branch/Bank/BranchManagement test =============");

        // 1) create a bank (constructor takes a String bankID param, which your Bank ignores)
        Bank myBank = new Bank("First National");

        System.out.println("Created bank:");
        System.out.println("  bankID (generated): " + myBank.getBankID());
        System.out.println("  name: " + myBank.getName());

        // 2) create BranchManagement
        BranchManagement manager = new BranchManagement();

        // 3) add branches using BranchManagement.addBranch
        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);
        Branch b2 = manager.addBranch("Uptown Branch", "456 Oak Ave", myBank);

        // 4) inspect bank branches via getBranches and getBranch
        System.out.println("\nBranches inside bank after additions:");
        for (int i = 0; i < myBank.getBranches().size(); i++) {
            Branch b = myBank.getBranch(i);
            System.out.println("  index " + i + ": id=" + b.getBranchID()
                + ", name=" + b.getBranchName()
                + ", address=" + b.getAddress());
        }

        // 5) test getBranchInfo (returns the branch object)
        System.out.println("\ngetBranchInfo for b1:");
        Branch info = manager.getBranchInfo(b1);
        System.out.println("  id=" + info.getBranchID() + ", name=" + info.getBranchName());

        // 6) test updateBranch
        System.out.println("\nUpdating b2 name/address...");
        manager.updateBranch(b2, "Uptown Financial Center", "789 Elm Blvd");
        System.out.println("  b2 now: id=" + b2.getBranchID() + ", name=" + b2.getBranchName()
            + ", address=" + b2.getAddress());

        // 7) test removeBranch (successful)
        System.out.println("\nRemoving b1 via manager.removeBranch...");
        boolean removed = manager.removeBranch(b1);
        System.out.println("  remove returned: " + removed);
        System.out.println("  branches now: " + myBank.getBranches().size());

        // 8) attempt to remove the same branch again (should return false)
        System.out.println("\nAttempting to remove b1 again...");
        boolean removedAgain = manager.removeBranch(b1);
        System.out.println("  remove returned: " + removedAgain);
        System.out.println("  branches now: " + myBank.getBranches().size());

        // 9) create a branch but do NOT add it to the bank, then try to remove it
        System.out.println("\nCreate branch not added to bank, then try to remove it...");
        Branch orphan = new Branch("Orphan Branch", "999 Nowhere Rd", myBank);
        boolean removedOrphan = manager.removeBranch(orphan);
        System.out.println("  removedOrphan: " + removedOrphan);
        System.out.println("  branches in bank still: " + myBank.getBranches().size());

        // 10) test getBranch with invalid index to show exception handling
        System.out.println("\nTesting getBranch with invalid index...");
        try {
            myBank.getBranch(10);
            System.out.println("  Successfully accessed index 10 (unexpected).");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("  Caught expected IndexOutOfBoundsException: " + ex.getMessage());
        }

        // 11) final listing
        System.out.println("\nFinal list of branches inside bank:");
        myBank.getBranches().forEach(b ->
            System.out.println("  id=" + b.getBranchID()
                + ", name=" + b.getBranchName()
                + ", address=" + b.getAddress())
        );

        System.out.println("\n============= Test complete =============");
    }

}


