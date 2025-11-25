package bank.user;

public class Customer extends User{
    
    public Customer(UserDetails userDetails) {
        super(userDetails, Role.CUSTOMER);
    }

    // Display customers info
    public void printCustomerInfo() {
        System.out.println("Customer's info: " );
        System.out.println("name: "+ super.getName());
    }
}

