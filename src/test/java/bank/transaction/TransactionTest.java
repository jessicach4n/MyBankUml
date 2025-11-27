package bank.transaction;

import bank.account.Card;
import bank.account.Check;
import bank.account.Saving;
import bank.transaction.Transaction;
import bank.user.Customer;
import bank.user.UserDetails;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Customer customer;
    private Card card;
    private Check check;
    private Saving saving;

    @BeforeEach
    void setup() {
        customer = new Customer(
                new UserDetails("John_Deer", "password123", "john@gmail.com", "John Deer"),
                10,
                "(123) 456-7891",
                null
        );

        card = new Card("123", 0, "Active", customer);
        check = new Check("456", 0, "Active", customer);
        saving = new Saving("789", 0, "Active", customer);

        customer.addAccount(card);
        customer.addAccount(check);
        customer.addAccount(saving);
    }

    @Test
    void testTransactionCreationAndAdd() {
        Transaction t1 = new Transaction("Deposit", "Success", 100, "Recipient", card);
        card.addTransaction(t1);

        List<Transaction> transactions = card.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(100, transactions.get(0).getAmount());
        assertEquals("Deposit", transactions.get(0).getType());
    }

    @Test
    void testDeposit() {
        card.deposit(500);
        check.deposit(1000);

        assertEquals(500, card.getBalance());
        assertEquals(1000, check.getBalance());

        assertEquals(1, card.getTransactions().size());
        assertEquals(1, check.getTransactions().size());
    }

    @Test
    void testPayAndReceipt() {
        card.deposit(200);
        card.pay();
        card.receipt();

        // Check if transaction exists
        List<Transaction> transactions = card.getTransactions();
        assertFalse(transactions.isEmpty());
    }

    @Test
    void testMultipleAccountTransactions() {
        card.deposit(100);
        check.deposit(200);
        saving.deposit(300);

        assertEquals(100, card.getBalance());
        assertEquals(200, check.getBalance());
        assertEquals(300, saving.getBalance());

        // Each account should have 1 transaction
        assertEquals(1, card.getTransactions().size());
        assertEquals(1, check.getTransactions().size());
        assertEquals(1, saving.getTransactions().size());
    }
}
