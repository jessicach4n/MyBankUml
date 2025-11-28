package bank.branch;

public class Branch {
    
    private static int idCounter = 0;
    private final int branchID;
    private String branchName;
    private String address;
    private transient Bank bank;

    // Constructor requires all four core attributes
    public Branch(String branchName, String address, Bank bank) {
        this.branchName = branchName;
        this.address = address;
        this.bank = bank;
        this.branchID = generateID();
    }

    private int generateID(){
        return ++idCounter;
    }
    
    public int getBranchID() {
        return branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getAddress() {
        return address;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}