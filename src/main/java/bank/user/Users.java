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
}