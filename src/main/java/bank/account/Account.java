package bank.account;

import bank.transaction.Transaction;
import bank.user.Customer;
import bank.utils.InternalLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    
    static long idCounter = 0;
    private long id;
    private String accountNumber;
    private String accountType;
    private double balance;
    private String status;
    protected Customer customer;
    private ArrayList<Transaction> transactions;

    private final InternalLogger logger = new InternalLogger();
    
    // Account can start off with having transactions or not
    public Account(String accountNumber, String accountType, double balance, String status, Customer customer) {
        this.id = generateId();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.customer = customer;
        this.transactions = new ArrayList<>();
    }

    public Account(String accountNumber, String accountType, double balance, String status) {
        this.id = generateId();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.transactions = new ArrayList<>();
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

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

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

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        logger.info(customer.getName() + " (id: " + customer.getId() + ") deposited " + amount + " to " + accountNumber);

        this.balance = this.balance + amount;

        // Add the transaction to the transaction list
        Transaction transaction = new Transaction("Deposit", "Completed", amount, customer.getName(), this);
        addTransaction(transaction);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        logger.info(customer.getName() + " (id: " + customer.getId() + ") withdrew " + amount + " from account " + accountNumber);
        this.balance = this.balance - amount;

        // Add the transaction to the transaction list
        Transaction transaction = new Transaction("Withdrawal", "Completed", amount, customer.getName(), this);
        addTransaction(transaction);
    }

    public abstract void pay();
    public abstract void receipt();

    @Override
    public String toString() {
        return "[\n" +
            "  Account ID: " + id + "\n" +
            "  Account Number: " + accountNumber + "\n" +
            "  Account Type: " + accountType + "\n" +
            "  Balance: " + balance + "\n" +
            "  Status: " + status + "\n" +
            "  Customer: " + (customer != null ? customer.getName() : "None") + "\n" +
            "  Transactions: " + transactions + "\n" +
            "]";
    }
}
