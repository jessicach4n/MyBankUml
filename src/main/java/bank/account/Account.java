package bank.account;

import bank.transaction.Transaction;
import bank.user.Customer;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    
    static long idCounter = 0;
    private long id;
    private String accountNumber;
    private String accountType;
    private double balance;
    private String status;
    protected Customer customer; // Should this be here?
    
    public Account(String accountNumber, String accountType, double balance, String status, Customer customer,
    List<Transaction> transactions) {
        this.id = generateId();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.customer = customer;
        this.transactions = transactions;
    }
    
    public long getId() {
        return id;
    }
    
    private long generateId() {
        return ++idCounter;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    protected List<Transaction> transactions;

    public Account(Customer customer) {
        this.customer = customer;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public abstract void pay();
    public abstract void receipt();
}

