package bank.branch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class BranchManager {

    private static final String BRANCH_FILE = "data/branches.json";
    private final Gson gson = new Gson();
    
    public BranchManager() {

    }

    // Load branches from JSON into a bank
    public void loadBranches(Bank bank) {
        try (Reader reader = new FileReader(BRANCH_FILE)) {
            Type type = new TypeToken<Map<String, List<Branch>>>() {}.getType();
            Map<String, List<Branch>> map = gson.fromJson(reader, type);
            if (map != null && map.containsKey("branches")) {
                List<Branch> loaded = map.get("branches");
                for (Branch b : loaded) {
                    b.setBank(bank);
                    bank.addBranch(b);
                }
            }
            System.out.println("[BranchManager]: Loaded " + bank.getBranches().size() + " branches from JSON.");
        } catch (FileNotFoundException e) {
            System.out.println("[BranchManager]: Branch JSON file not found. Starting with empty list.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save branches to JSON
    private void saveBranches(Bank bank) {
        try (Writer writer = new FileWriter(BRANCH_FILE)) {
            Map<String, List<Branch>> map = Map.of("branches", bank.getBranches());
            gson.toJson(map, writer);
            System.out.println("[BranchManager]: Saved " + bank.getBranches().size() + " branches to JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add branch and save
    public Branch addBranch(String name, String address, Bank bank) {
        Branch branch = new Branch(name, address, bank);
        bank.addBranch(branch);
        saveBranches(bank);
        System.out.println("[BranchManager]: Branch added: " + name);
        return branch;
    }

    // remove branch and then save
    public boolean removeBranch(Branch branch) {
        Bank bank = branch.getBank();
        if (bank == null) {
            System.out.println("[BranchManager]: Bank is null");
            return false;
        }

        boolean success = bank.getBranches().removeIf(b -> b.getBranchID() == branch.getBranchID());
        if (success) {
            saveBranches(bank);
            System.out.println("[BranchManager]: Branch removed: " + branch.getBranchName());
        } else {
            System.out.println("[BranchManager]: Could not remove branch: " + branch.getBranchID());
        }
        return success;
    }

    public List<Branch> getBranchList(Bank bank) {
        return bank.getBranches();
    }

    public void updateBranch(Branch branch, String newName, String newAddress) {
        branch.setBranchName(newName);
        branch.setAddress(newAddress);
        saveBranches(branch.getBank());
        System.out.println("[BranchManager]: Branch updated: " + branch.getBranchName());
    }

    public Branch getBranchInfo(Branch branch) {
        return branch;
    }
}