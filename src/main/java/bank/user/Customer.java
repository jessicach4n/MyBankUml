package bank.user;

import java.util.ArrayList;
import bank.account.Account;

public class Customer extends User {

    private int age;
    private String telNo;
    
    // Instantiate Customer with no accounts
    public Customer(UserDetails userDetails, int age, String telNo, ArrayList<Account> accounts) {
        super(userDetails, Role.CUSTOMER);
        this.age = age;
        this.telNo = telNo;
        if (accounts == null) {
            this.accounts = new ArrayList<>();
        } else {
            this.accounts = accounts;
        }
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
        System.out.println("Customer Information of: " + super.getName());
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
