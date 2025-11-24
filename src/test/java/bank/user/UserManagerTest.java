package bank.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setup() {
        userManager = new UserManager();
    }

    @Test
    void testAdminCreatesUsers() throws Exception {
        // Admin creating a Teller and a Customer
        User admin = userManager.createUser("adminUser", "adminPass", "admin@example.com", "Admin Name", 35, "123456789", Role.ADMIN, Role.ADMIN);
        User teller = userManager.createUser("tellerUser", "tellerPass", "teller@example.com", "Teller Name", 30, "987654321", Role.TELLER, Role.ADMIN);
        Customer customer = (Customer) userManager.createUser("customerUser", "customerPass", "customer@example.com", "Shayan Aminaei", 10, "100", Role.CUSTOMER, Role.TELLER);

        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.TELLER, teller.getRole());
        assertEquals(Role.CUSTOMER, customer.getRole());
    }

    @Test
    void testAssignRole() throws Exception {
        User admin = userManager.createUser("adminUser", "adminPass", "admin@example.com", "Admin Name", 35, "123456789", Role.ADMIN, Role.ADMIN);
        User genUser = userManager.createUser("genericUser", "genPass", "generic@example.com", "Generic Name", 25, "555555555", Role.CUSTOMER, admin.getRole());

        // Admin assigns a new role
        userManager.assignRole(genUser.getId(), Role.TELLER, admin.getRole());
        assertEquals(Role.TELLER, genUser.getRole());

        // Teller cannot assign roles (should throw an exception)
        User teller = userManager.createUser("tellerUser", "tellerPass", "teller@example.com", "Teller Name", 30, "987654321", Role.TELLER, admin.getRole());
        Exception exception = assertThrows(Exception.class, () -> {
            userManager.assignRole(genUser.getId(), Role.CUSTOMER, teller.getRole());
        });
        assertTrue(exception.getMessage().contains("Only administrators can assign roles"));
    }
}
