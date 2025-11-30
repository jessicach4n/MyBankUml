package bank.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Users {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls().setPrettyPrinting().create();

    private static final Logger LOGGER = Logger.getLogger(Users.class.getName());

    private static List<User> USERS = new ArrayList<>();
    private static Map<Long, User> USER_MAP = new HashMap<>();

    private static final Path DATA_JSON = Path.of("data/users.json");
    private static Path jsonFile = DATA_JSON;

    public static void setJsonFile(Path path) {
        jsonFile = path;
    }

    public record User(
            long id,
            String username,
            String name,
            String role,
            String password,
            String email,
            List<Account> accounts
    ) {}

    public record Account(
            String number,
            String type,
            double balance,
            List<Transaction> transactions
    ) {}

    public record Transaction(
            long date,
            double amount,
            String details,
            String to_account,
            String from_account,
            long recipient_id,
            String recipient_name
    ) {}

    public static void load()
    {
        try (Reader reader = Files.newBufferedReader(jsonFile)) {
            User[] arr = GSON.fromJson(reader, User[].class);
            if (arr == null) arr = new User[0];

            USERS = new ArrayList<>(List.of(arr));
            USER_MAP = new HashMap<>();
            for (User u : USERS) {
                USER_MAP.put(u.id(), u);
            }

            LOGGER.info("Loaded " + USERS.size() + " users from " + jsonFile.toAbsolutePath());
            return;
        } catch (Exception e) {
            LOGGER.warning("Could not load from " + jsonFile.toAbsolutePath() + ", trying resources");
        }

        // Fallback: try loading from resources folder
        try (Reader r = new java.io.InputStreamReader(
                Users.class.getResourceAsStream("/bank/users.json")))
        {
            if (r != null) {
                User[] arr = GSON.fromJson(r, User[].class);
                if (arr != null) {
                    USERS = new ArrayList<>(List.of(arr));
                    USER_MAP = new HashMap<>();
                    for (User u : USERS) {
                        USER_MAP.put(u.id(), u);
                    }
                    LOGGER.info("Loaded " + USERS.size() + " users from resources");
                    // Save to data folder immediately so future loads use the same file
                    save();
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load users from resources", e);
        }

        // if both fail, start with empty list
        USERS = new ArrayList<>();
        USER_MAP = new HashMap<>();
        LOGGER.warning("Starting with empty user list");
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(jsonFile)) {
            GSON.toJson(USERS, writer);
            LOGGER.info("Saved " + USERS.size() + " users to " + jsonFile.toAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save users to " + jsonFile.toAbsolutePath(), e);
        }
    }

    public static void add(User user) {
        USERS.add(user);
        USER_MAP.put(user.id(), user);
        LOGGER.info("Added user: " + user.username() + " (ID " + user.id() + ")");
    }

    public static List<User> get() {
        return USERS;
    }

    public static User get(long id) {
        return USER_MAP.get(id);
    }

    public static void reset() {
        USERS.clear();
        USER_MAP.clear();
        LOGGER.info("Reset in-memory users.");
    }

    /**
     * Execute a transaction between two accounts (can be same user or different users)
     * Creates two transactions: withdrawal from account 1, deposit to account 2
     * Updates both account balances and saves to JSON
     *
     * @param u1 User 1 ID (sender)
     * @param a1 Account 1 number (from)
     * @param u2 User 2 ID (receiver)
     * @param a2 Account 2 number (to)
     * @param amount Amount to transfer
     * @param details Transaction description
     */
    public static void transaction(long u1, String a1, long u2, String a2, double amount, String details) {
        // Validate amount
        if (amount <= 0) {
            LOGGER.warning("Transaction failed: amount must be positive");
            return;
        }

        long date = bank.Convert.date(java.time.LocalDate.now().toString());

        User U1 = Users.get(u1);
        User U2 = Users.get(u2);

        if (U1 == null || U2 == null) {
            LOGGER.warning("Transaction failed: user not found (U1: " + u1 + ", U2: " + u2 + ")");
            return;
        }

        // Find accounts using helper method
        Account A1 = findAccount(U1, a1);
        Account A2 = findAccount(U2, a2);

        if (A1 == null || A2 == null) {
            LOGGER.warning("Transaction failed: account not found (A1: " + a1 + ", A2: " + a2 + ")");
            return;
        }

        // Check sufficient balance
        if (A1.balance() < amount) {
            LOGGER.warning("Transaction failed: insufficient balance (has: " + A1.balance() + ", needs: " + amount + ")");
            return;
        }

        // Create transaction records
        Transaction T1 = new Transaction(
                date,
                -amount,
                details,
                A2.number(),
                A1.number(),
                U2.id(),
                U2.name()
        );
        Transaction T2 = new Transaction(
                date,
                amount,
                details,
                A2.number(),
                A1.number(),
                U1.id(),
                U1.name()
        );

        // Create NEW transaction lists with defensive copies
        List<Transaction> newT1List = new ArrayList<>(A1.transactions());
        newT1List.add(T1);

        List<Transaction> newT2List = new ArrayList<>(A2.transactions());
        newT2List.add(T2);

        // Create new account records with updated balances and NEW lists
        Account A1b = new Account(
                A1.number(),
                A1.type(),
                A1.balance() - amount,
                newT1List
        );

        Account A2b = new Account(
                A2.number(),
                A2.type(),
                A2.balance() + amount,
                newT2List
        );

        // Replace accounts using helper method
        replaceAccount(U1, A1, A1b);
        replaceAccount(U2, A2, A2b);

        Users.save();
        LOGGER.info("Transaction completed: " + amount + " from " + a1 + " to " + a2);
    }

    /**
     * Execute a withdrawal to an external recipient (non-user)
     * Creates one withdrawal transaction and updates account balance
     *
     * @param userId User ID
     * @param accountNumber Account number to withdraw from
     * @param amount Amount to withdraw
     * @param recipientName External recipient name
     * @param details Transaction description
     */
    public static void withdraw(long userId, String accountNumber, double amount, String recipientName, String details) {
        // Validate amount
        if (amount <= 0) {
            LOGGER.warning("Withdrawal failed: amount must be positive");
            return;
        }

        long date = bank.Convert.date(java.time.LocalDate.now().toString());

        // Get user
        User user = Users.get(userId);
        if (user == null) {
            LOGGER.warning("Withdrawal failed: user not found (ID: " + userId + ")");
            return;
        }

        // Find account using helper method
        Account account = findAccount(user, accountNumber);
        if (account == null) {
            LOGGER.warning("Withdrawal failed: account not found (" + accountNumber + ")");
            return;
        }

        // Check sufficient balance
        if (account.balance() < amount) {
            LOGGER.warning("Withdrawal failed: insufficient balance (has: " + account.balance() + ", needs: " + amount + ")");
            return;
        }

        // Create withdrawal transaction (negative amount, external recipient)
        Transaction withdrawal = new Transaction(
                date,
                -amount,
                details,
                "0",  // No destination account (external)
                accountNumber,
                0,  // No recipient ID (external)
                recipientName
        );

        // Create NEW transaction list with defensive copy
        List<Transaction> newTransactionList = new ArrayList<>(account.transactions());
        newTransactionList.add(withdrawal);

        // Update account with new balance and NEW list
        Account updatedAccount = new Account(
                account.number(),
                account.type(),
                account.balance() - amount,
                newTransactionList
        );

        // Replace account using helper method
        replaceAccount(user, account, updatedAccount);

        Users.save();
        LOGGER.info("Withdrawal completed: " + amount + " from " + accountNumber + " to " + recipientName);
    }

    /**
     * Execute a deposit from an external source (non-user).
     * Creates one deposit transaction and updates account balance.
     *
     * @param userId User ID
     * @param accountNumber Account number to deposit to
     * @param amount Amount to deposit
     * @param senderName External sender name
     * @param details Transaction description
     */
    public static void deposit(long userId, String accountNumber, double amount, String senderName, String details) {
        // Validate amount
        if (amount <= 0) {
            LOGGER.warning("Deposit failed: amount must be positive");
            return;
        }

        long date = bank.Convert.date(java.time.LocalDate.now().toString());

        // Get user
        User user = Users.get(userId);
        if (user == null) {
            LOGGER.warning("Deposit failed: user not found (ID: " + userId + ")");
            return;
        }

        // Find account using helper method
        Account account = findAccount(user, accountNumber);
        if (account == null) {
            LOGGER.warning("Deposit failed: account not found (" + accountNumber + ")");
            return;
        }

        // Create deposit transaction (positive amount, external sender)
        Transaction deposit = new Transaction(
                date,
                amount,
                details,
                accountNumber,
                "0",
                0,
                senderName
        );

        // Create new transaction list with defensive copy
        List<Transaction> newTransactionList = new ArrayList<>(account.transactions());
        newTransactionList.add(deposit);

        // Update account with new balance and new list
        Account updatedAccount = new Account(
                account.number(),
                account.type(),
                account.balance() + amount,
                newTransactionList
        );

        // Replace account using helper method
        replaceAccount(user, account, updatedAccount);

        Users.save();
        LOGGER.info("Deposit completed: " + amount + " to " + accountNumber + " from " + senderName);
    }

    /**
     * Helper method to find an account by number within a user's accounts
     *
     * @param user The user whose accounts to search
     * @param accountNumber The account number to find
     * @return The account if found, null otherwise
     */
    private static Account findAccount(User user, String accountNumber) {
        if (user == null || accountNumber == null) {
            return null;
        }
        for (Account account : user.accounts()) {
            if (account.number().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Helper method to replace an account in a user's account list
     *
     * @param user The user whose account to replace
     * @param oldAccount The account to replace
     * @param newAccount The new account
     */
    private static void replaceAccount(User user, Account oldAccount, Account newAccount) {
        if (user == null || oldAccount == null || newAccount == null) {
            return;
        }
        int index = user.accounts().indexOf(oldAccount);
        if (index >= 0) {
            user.accounts().set(index, newAccount);
        }
    }
}