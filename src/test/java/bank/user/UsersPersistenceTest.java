package bank.user;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersPersistenceTest {

    private static final Path DATA_JSON = Path.of("data/users.json");

    @BeforeEach
    void setup() throws Exception {
        // Copy the resource template (with 7 users: 3 customers + 4 staff) to data/users.json
        var resource = getClass().getClassLoader().getResource("bank/users.json");
        assertNotNull(resource, "users.json resource not found");

        Files.createDirectories(DATA_JSON.getParent());
        Files.copy(Path.of(resource.toURI()), DATA_JSON, StandardCopyOption.REPLACE_EXISTING);

        // Point Users to the writable JSON
        Users.setJsonFile(DATA_JSON);

        // Reset memory and load users
        Users.reset();
        Users.load();
    }

    @Test
    void testAddUserAndPersist() {
        // Add a new user (Dave) - note: ID 4 conflicts with existing johndoe, using ID 8
        Users.User newUser = new Users.User(
                8,
                "daveUser",
                "Dave",
                "CUSTOMER",
                "pass789",
                "dave@email.com",
                List.of()
        );

        Users.add(newUser);
        Users.save(); // now data/users.json contains 7 existing users + 1 new = 8

        // Clear memory and reload
        Users.reset();
        Users.load();

        // Verify we now have 8 users
        List<Users.User> users = Users.get();
        assertEquals(8, users.size(), "Should have 8 users after adding Dave");

        // Verify Dave is present
        Users.User addedUser = users.stream()
                .filter(u -> u.id() == 8)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Added user not found"));

        assertEquals("Dave", addedUser.name());
        assertEquals("daveUser", addedUser.username());
    }
}
