package bank.branch;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private static int idCounter = 0;
    private final int bankID;
    private final String name;
    private final List<Branch> branches;

    public Bank(String name) {
        this.bankID = generateID();
        this.name = name;
        this.branches = new ArrayList<>();
    }

    private int generateID(){
        return ++idCounter;
    }

    // Method used by BranchManagement.addBranch
    public void addBranch(Branch branch) {
        if (!branches.contains(branch)) {
            branches.add(branch);
        } else {
            System.out.println("Branch " + branch.getBranchName() + " (id: " + branch.getBranchID() + ") is already in the bank.");
        }
    }

    // Method used by BranchManagement.removeBranch
    // Returns true if it was successfully removed
    public boolean removeBranch(Branch branch) {
        return branches.remove(branch);
    }
    
    public Branch getBranch(int index){
        return branches.get(index);
    }

    public int getBankID() {
        return bankID;
    }

    public String getName() {
        return name;
    }
    
    public List<Branch> getBranches() {
        return branches;
    }
}