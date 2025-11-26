package bank.branch;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BranchManagerTest {

    @Test
    void testCreateBank() {
        Bank myBank = new Bank("First National");

        assertNotNull(myBank.getBankID());
        assertEquals("First National", myBank.getName());
        assertNotNull(myBank.getBranches());
    }

    @Test
    void testAddBranches() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);
        Branch b2 = manager.addBranch("Uptown Branch", "456 Oak Ave", myBank);

        List<Branch> branches = myBank.getBranches();

        assertEquals(2, branches.size());
        assertEquals(b1, branches.get(0));
        assertEquals(b2, branches.get(1));
    }

    @Test
    void testGetBranchInfo() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);

        Branch info = manager.getBranchInfo(b1);

        assertEquals(b1.getBranchID(), info.getBranchID());
        assertEquals("Downtown Branch", info.getBranchName());
    }

    @Test
    void testUpdateBranch() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b2 = manager.addBranch("Uptown Branch", "456 Oak Ave", myBank);

        manager.updateBranch(b2, "Uptown Financial Center", "789 Elm Blvd");

        assertEquals("Uptown Financial Center", b2.getBranchName());
        assertEquals("789 Elm Blvd", b2.getAddress());
    }

    @Test
    void testRemoveBranchSuccess() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);

        boolean removed = manager.removeBranch(b1);

        assertTrue(removed);
        assertEquals(0, myBank.getBranches().size());
    }

    @Test
    void testRemoveBranchFailsIfAlreadyRemoved() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);

        manager.removeBranch(b1);
        boolean removedAgain = manager.removeBranch(b1);

        assertFalse(removedAgain);
        assertEquals(0, myBank.getBranches().size());
    }

    @Test
    void testRemoveOrphanBranch() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        // orphan branch not added to the bank
        Branch orphan = new Branch("Orphan Branch", "999 Nowhere Rd", myBank);

        boolean removedOrphan = manager.removeBranch(orphan);

        assertFalse(removedOrphan);
        assertEquals(0, myBank.getBranches().size());
    }

    @Test
    void testGetBranchInvalidIndexThrows() {
        Bank myBank = new Bank("First National");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            myBank.getBranch(10);
        });
    }

    @Test
    void testFinalBranchList() {
        Bank myBank = new Bank("First National");
        BranchManager manager = new BranchManager();

        Branch b1 = manager.addBranch("Downtown Branch", "123 Main St", myBank);

        List<Branch> branches = myBank.getBranches();

        assertEquals(1, branches.size());
        assertEquals("Downtown Branch", branches.get(0).getBranchName());
    }
}
