package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Users {

    private static final Gson GSON = new GsonBuilder()
    .serializeNulls().setPrettyPrinting().create();

    private static List<User> USERS = new ArrayList<>();
    private static Map<Long, User> USER_MAP = new HashMap<>();

    public record User(
            long id,
            String name,
            String role,
            String password,
            List<Account> accounts
    ) {}

    public record Account(
            long number,
            String type,
            long balance,
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
        Path path = Path.of("users.json");
        try (Reader r = Files.newBufferedReader(path)) 
        {
            User[] arr = GSON.fromJson(r, User[].class);
            if (arr == null) 
            {
                USERS = new ArrayList<>();
                USER_MAP = new HashMap<>();
                return;
            }
            USERS = new ArrayList<>(List.of(arr));
            USER_MAP = new HashMap<>();
            for (User u : USERS)
            {
                USER_MAP.put(u.id(), u);
            }
        } catch (Exception e) {
            USERS = new ArrayList<>();
            USER_MAP = new HashMap<>();
        }
    }
    
    public static void save() 
    {
        Path path = Path.of("users.json");
        try (Writer w = Files.newBufferedWriter(path)) 
        {
            GSON.toJson(USERS, w);
        } catch (Exception ignored) {}
    }

    public static List<User> get() 
    {
        return USERS;
    }

    public static User get(long id) 
    {
        return USER_MAP.get(id);
    }
}