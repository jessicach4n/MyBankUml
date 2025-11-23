package bank.user;
import bank.branch.Branch;

public class Employee extends User{
    private long employeeId;
    private Branch branch;

    public Employee(String username, String password, String emailString, Role role, long employeeId, Branch branch) {
        super(username, password, emailString, role);
        super.setId(employeeId);
        this.branch = branch;
    }

    public long getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }
    public Branch getBranch() {
        return branch;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
