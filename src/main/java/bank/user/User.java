package bank.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import bank.account.Account;


public abstract class User {
    private static long idCounter = 0;
    private long id;
    private Role role;
    private Date createdDate;
    private UserDetails details;
    protected List<Account> accounts;

    public User(long id, UserDetails details, Role role, Date createdDate, List<Account> accounts) {
        this.id = id;
        this.details = details;
        this.role = role;
        this.createdDate = createdDate;
        this.accounts = accounts != null ? new ArrayList<>(accounts) : new ArrayList<>();
    }

    public User(UserDetails details, Role role) {
        this.id = generateId();
        this.details = details;
        this.role = role;
        this.createdDate = new Date();
        this.accounts = new ArrayList<>();
    }

    public void setDetails(UserDetails details) {
        if (details == null) throw new IllegalArgumentException("UserDetails cannot be null");
        this.details = details;
    }

    public void updateDetails(UserDetails newDetails) {
        setUsername(newDetails.getUsername());
        setPassword(newDetails.getPassword());
        setEmailString(newDetails.getEmail());
        setName(newDetails.getName());
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

    public String getUsername() {
        return this.details.getUsername();
    }

    public void setUsername(String username) {
        this.details.setUsername(username);
    }

    public String getPassword() {
        return this.details.getPassword();
    }

    public void setPassword(String password) {
        this.details.setPassword(password);
    }

    public String getEmailString() {
        return this.details.getEmail();
    }

    public void setEmailString(String emailString) {
        this.details.setEmail(emailString);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() { return this.details.getName(); }

    public void setName(String newName) { this.details.setName(newName); }

    public boolean validatePassword() {
        // TODO
        return true;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void removeAccount(String accountNumber) {
        this.accounts.removeIf(a -> a.getAccountNumber().equals(accountNumber));
    }
}