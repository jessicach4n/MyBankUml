package bank.user;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UsersJsonTest {

    private static final Path TEST_JSON = Path.of("data/users_test.json");

    @BeforeEach
    void setup() throws Exception {
        // Copy resource template to test JSON
        var resource = getClass().getClassLoader().getResource("bank/users.json");
        assertNotNull(resource, "users.json resource not found");

        Files.createDirectories(TEST_JSON.getParent());
        Files.copy(Path.of(resource.toURI()), TEST_JSON, StandardCopyOption.REPLACE_EXISTING);

        Users.setJsonFile(TEST_JSON);
        Users.reset();
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

    @Test
    void testLoadUsersWithTransactions() {
        Users.load();

        List<Users.User> users = Users.get();
        assertEquals(7, users.size(), "Should load 7 users");

        // Check Kanye
        Users.User kanye = users.get(0);
        assertEquals(1, kanye.id());
        assertEquals("Kanye West", kanye.name());
        assertEquals("CUSTOMER", kanye.role());
        assertEquals(1, kanye.accounts().size());

        var kanyeAccount = kanye.accounts().get(0);
        assertEquals("Checking", kanyeAccount.type());
        assertEquals(2500000.0, kanyeAccount.balance());
        assertEquals(4, kanyeAccount.transactions().size());

        var firstTransaction = kanyeAccount.transactions().get(0);
        assertEquals(-150000.0, firstTransaction.amount());
        assertEquals("Studio equipment", firstTransaction.details());

        // Check Kubrick
        Users.User kubrick = users.get(1);
        assertEquals(2, kubrick.id());
        assertEquals("Stanley Kubrick", kubrick.name());
        assertEquals(1, kubrick.accounts().size());

        var kubrickAccount = kubrick.accounts().get(0);
        assertEquals("Savings", kubrickAccount.type());
        assertEquals(4, kubrickAccount.transactions().size());

        // Check Beethoven
        Users.User beethoven = users.get(2);
        assertEquals(3, beethoven.id());
        assertEquals("Ludwig van Beethoven", beethoven.name());
        assertEquals(1, beethoven.accounts().size());

        var beethovenAccount = beethoven.accounts().get(0);
        assertEquals("Checking", beethovenAccount.type());
        assertEquals(1500000.0, beethovenAccount.balance());
        assertEquals(4, beethovenAccount.transactions().size());
    }
}