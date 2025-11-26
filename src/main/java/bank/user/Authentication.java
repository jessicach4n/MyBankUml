package bank.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Loads users from the bundled JSON file and authenticates using a HashMap of userId -> password.
 */
public class Authentication {
    private static final Gson GSON = new GsonBuilder().create();
    private final Map<Long, Users.User> usersById = new HashMap<>();
    private final Map<Long, String> passwordsById = new HashMap<>();

    public Authentication() {
        loadUsers();
    }

    private void loadUsers() {
        try (Reader reader = new InputStreamReader(
                Authentication.class.getResourceAsStream("/bank/users.json"))) {
            Users.User[] loaded = GSON.fromJson(reader, Users.User[].class);
            if (loaded == null) {
                return;
            }
            for (Users.User user : loaded) {
                usersById.put(user.id(), user);
                passwordsById.put(user.id(), user.password());
            }
        } catch (Exception e) {
            // swallow to keep UI from crashing; real app would log
        }
    }

    public Optional<Users.User> authenticate(long userId, String password) {
        String expected = passwordsById.get(userId);
        if (expected == null) {
            return Optional.empty();
        }
        if (!expected.equals(password)) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersById.get(userId));
    }
}
