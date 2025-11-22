package transaction;

import java.util.Date;
import account.Account;

public class Transaction {
    private static long idCounter = 0;
    private long id;
    private Date date;
    private String type;
    private String status;
    private double amount;
    private String recipient;
    private Account account;

    // Should the date be a parameter??
    public Transaction(String type, String status, double amount, String recipient, Account account) {
        this.id = generateId();
        this.date = new Date();
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.recipient = recipient;
        this.account = account;
    }

    private long generateId() {
        return ++idCounter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void pay() {
        System.out.println("Payment transaction is done.");
    }

    public void receipt() {
        System.out.println("Transaction receipt.");
    }
}
