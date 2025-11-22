package bank.user;
import bank.branch.Branch;

public class Employee extends User {
    private Branch branch;

    public Employee(String username, String password, String email, Role role, Branch branch) {
        super(username, password, email, role);
        this.branch = branch;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
