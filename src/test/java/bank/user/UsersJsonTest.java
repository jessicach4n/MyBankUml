package bank.user;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UsersJsonTest {

    @BeforeEach
    void setup() {
        // Clear existing users before each test
        Users.get().clear();
    }

    @Test
    void testSaveAndLoadUsers() {
        // Create Users.User objects
        Users.User user1 = new Users.User(
                1,
                "aliceUser",
                "Alice",
                "CUSTOMER",
                "password123",
                "alice@email.com",
                List.of()
        );

        Users.User user2 = new Users.User(
                2,
                "bobUser",
                "Bob",
                "TELLER",
                "pass456",
                "bob@email.com",
                List.of()
        );

        // Add users
        Users.add(user1);
        Users.add(user2);

        // Save to JSON
        Users.save();

        // Clear the in-memory list to simulate fresh start
        Users.get().clear();

        assertEquals(0, Users.get().size(), "Users list should be empty after clearing");

        // Load from JSON
        Users.load();

        // Verify that users are loaded correctly
        assertEquals(2, Users.get().size(), "Should load 2 users from JSON");

        Users.User loadedUser1 = Users.get().get(0);
        assertEquals("Alice", loadedUser1.name());
        assertEquals("aliceUser", loadedUser1.username());
        assertEquals("CUSTOMER", loadedUser1.role());

        Users.User loadedUser2 = Users.get().get(1);
        assertEquals("Bob", loadedUser2.name());
        assertEquals("bobUser", loadedUser2.username());
        assertEquals("TELLER", loadedUser2.role());
    }
}