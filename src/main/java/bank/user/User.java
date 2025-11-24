package bank.user;
import java.util.Date;

public abstract class User {
    private static long idCounter = 0;
    private long id;
    private String username;
    private String password;
    private String emailString;
    private Role role;
    private Date createdDate;
    
    public User(String username, String password, String emailString, Role role) {
        // ID is generated automatically, upon the instantiation of the User
        this.id = generateId();
        this.username = username;
        this.password = password;
        this.emailString = emailString;
        this.role = role;
        // Created date generated automatically, upon the instantiation of the User
        this.createdDate = new Date(); 
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
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailString() {
        return emailString;
    }

    public void setEmailString(String emailString) {
        this.emailString = emailString;
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

    public boolean validatePassword() {
        // TODO
        return true;
    }


}