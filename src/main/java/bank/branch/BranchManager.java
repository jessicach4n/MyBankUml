package bank.branch;

// This implementation is super not robust, but I think its ok
public class BranchManager {
    
    public BranchManager() {

    }

    // Return the newly created Branch object
    public Branch addBranch(String branchName, String address, Bank bank) {
        Branch newBranch = new Branch(branchName, address, bank);
        bank.addBranch(newBranch);
        System.out.println("Branch added successfully: " + branchName);
        return newBranch;
    }

    // Returns true if the branch was found and removed, false otherwise.
    public boolean removeBranch(Branch branch) {
        return branch.getBank().removeBranch(branch); 
    }

    public Branch getBranchInfo(Branch branch) {
        return branch;
    }

    public void updateBranch(Branch branch, String newName, String newAddress) {
        branch.setAddress(newAddress);
        branch.setBranchName(newName);
    }
}