package bank.user;

import java.util.ArrayList;
import bank.account.Account;

public class Customer extends User{

    private String name;
    private int age;
    private String telNo;
    private ArrayList<Account> accounts; // Each customer has a list of accounts (Check, Card etc.)
    
    // Instantiate Customer with no accounts
    public Customer(String username, String password, String emailString,
                    String name, int age, String telNo) {
        super(username, password, emailString, Role.CUSTOMER);
        this.name = name;
        this.age = age;
        this.telNo = telNo;
        this.accounts = new ArrayList<>();
    }

    // Instantiate Customer with no accounts
    public Customer(String username, String password, String emailString,
                    String name, int age, String telNo, ArrayList<Account> accounts) {
        this(username, password, emailString, name, age, telNo);
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void removeAccount(Account account) {
        this.accounts.remove(account);
    }

    public Account getAccount(int index) {
        return this.accounts.get(index);
    }

    public void setName(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    // Display customers info
    public void printCustomerInfo() {
        System.out.println("=================================");
        System.out.println("Customer Information of: " + name);
        System.out.println("Age: " + age);
        System.out.println("Telephone: " + telNo);
        System.out.println("Number of Accounts: " + accounts.size());
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println("Account [" + i + "]: " + acc);
        }
        System.out.println("=================================");
        System.out.println();
    }
}

