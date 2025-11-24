package bank.user;

public class Customer extends User{

    private String name;
    private int age;
    private String telNo;
    
    public Customer(String username, String password, String emailString, String name, int age, String telNo) {
        super(username, password, emailString, Role.CUSTOMER);
        this.name = name;
        this.age = age;
        this.telNo = telNo;
    }

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
        System.out.println("Customer's info: " );
        System.out.println("name: "+ name);
    }
}

