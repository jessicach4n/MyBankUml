package bank.user;

public class UserDetails {
    private String username;
    private String password;
    private String email;
    private String name;
    private int age;
    private String tel;

    public UserDetails(String username, String password, String email, String name, int age, String tel) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.age = age;
        this.tel = tel;
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
}
