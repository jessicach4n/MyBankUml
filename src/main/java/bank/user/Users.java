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
            long amount,
            String details,
            long to_account,
            long from_account,
            long recipient_id,
            String recipient_name
    ) {}

    public static void load()
    {
        // Try loading from jsonFile path (data/users.json or custom path) first
        // This ensures we read from the same location we write to
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

        // Fallback: try loading from resources folder (for initial setup)
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

        // If both fail, start with empty list
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
     * Execute a transaction between two accounts (can be same user or different users).
     * Creates two transactions: withdrawal from account 1, deposit to account 2.
     * Updates both account balances and saves to JSON.
     *
     * @param u1 User 1 ID (sender)
     * @param a1 Account 1 number (from)
     * @param u2 User 2 ID (receiver)
     * @param a2 Account 2 number (to)
     * @param amount Amount to transfer
     * @param details Transaction description
     */
    public static void transaction(long u1, String a1, long u2, String a2, double amount, String details) {
        long date = bank.Convert.date(java.time.LocalDate.now().toString());

        User U1 = Users.get(u1);
        User U2 = Users.get(u2);

        Account A1 = null;
        Account A2 = null;
        for (Account a : U1.accounts()) {
            if (a.number().equals(a1)) {
                A1 = a;
                break;
            }
        }
        for (Account a : U2.accounts()) {
            if (a.number().equals(a2)) {
                A2 = a;
                break;
            }
        }
        if (A1 == null || A2 == null) {
            LOGGER.warning("Transaction failed: account not found");
            return;
        }

        Transaction T1 = new Transaction(
                date,
                (long) -amount,
                details,
                Long.parseLong(A2.number()),
                Long.parseLong(A1.number()),
                U2.id(),
                U2.name()
        );
        Transaction T2 = new Transaction(
                date,
                (long) amount,
                details,
                Long.parseLong(A2.number()),
                Long.parseLong(A1.number()),
                U1.id(),
                U1.name()
        );

        A1.transactions().add(T1);
        A2.transactions().add(T2);

        Account A1b = new Account(
                A1.number(),
                A1.type(),
                A1.balance() - amount,
                A1.transactions()
        );

        Account A2b = new Account(
                A2.number(),
                A2.type(),
                A2.balance() + amount,
                A2.transactions()
        );

        int i1 = U1.accounts().indexOf(A1);
        int i2 = U2.accounts().indexOf(A2);

        U1.accounts().set(i1, A1b);
        U2.accounts().set(i2, A2b);

        Users.save();
        LOGGER.info("Transaction completed: " + amount + " from " + a1 + " to " + a2);
    }

    /**
     * Execute a withdrawal to an external recipient (non-user).
     * Creates one withdrawal transaction and updates account balance.
     *
     * @param userId User ID
     * @param accountNumber Account number to withdraw from
     * @param amount Amount to withdraw
     * @param recipientName External recipient name
     * @param details Transaction description
     */
    public static void withdraw(long userId, String accountNumber, double amount, String recipientName, String details) {
        long date = bank.Convert.date(java.time.LocalDate.now().toString());

        User user = Users.get(userId);
        if (user == null) {
            LOGGER.warning("Withdrawal failed: user not found");
            return;
        }

        Account account = null;
        for (Account a : user.accounts()) {
            if (a.number().equals(accountNumber)) {
                account = a;
                break;
            }
        }
        if (account == null) {
            LOGGER.warning("Withdrawal failed: account not found");
            return;
        }

        // Create withdrawal transaction (negative amount, external recipient)
        Transaction withdrawal = new Transaction(
                date,
                (long) -amount,
                details,
                0,  // No destination account (external)
                Long.parseLong(accountNumber),
                0,  // No recipient ID (external)
                recipientName
        );

        account.transactions().add(withdrawal);

        // Update account with new balance
        Account updatedAccount = new Account(
                account.number(),
                account.type(),
                account.balance() - amount,
                account.transactions()
        );

        int index = user.accounts().indexOf(account);
        user.accounts().set(index, updatedAccount);

        Users.save();
        LOGGER.info("Withdrawal completed: " + amount + " from " + accountNumber + " to " + recipientName);
    }
}