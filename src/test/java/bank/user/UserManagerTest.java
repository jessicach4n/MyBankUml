package bank.user;

import bank.user.repository.JsonUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setup() {
        userManager = new UserManager(new JsonUserRepository());
    }

    @Test
    void testValidateUserCreationRights() {
        assertTrue(userManager.validateUserCreationRights(Role.ADMIN, Role.TELLER));
        assertTrue(userManager.validateUserCreationRights(Role.ADMIN, Role.CUSTOMER));
        assertFalse(userManager.validateUserCreationRights(Role.TELLER, Role.ADMIN));
        assertFalse(userManager.validateUserCreationRights(Role.CUSTOMER, Role.CUSTOMER));
    }


    @Test
    void testAdminCreatesUsers() throws Exception {
        UserDetails adminDetails = new UserDetails("adminUser", "adminPass", "admin@example.com", "Admin Name");
        UserDetails tellerDetails = new UserDetails("tellerUser", "tellerPass", "teller@example.com", "Teller Name");
        UserDetails customerDetails = new UserDetails("customerUser", "customerPass", "customer@example.com", "Shayan Aminaei");

        User admin = userManager.createUser(adminDetails, Role.ADMIN, Role.ADMIN);
        User teller = userManager.createUser(tellerDetails, Role.TELLER, Role.ADMIN);
        Customer customer = (Customer) userManager.createUser(customerDetails, Role.CUSTOMER, Role.TELLER);

        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.TELLER, teller.getRole());
        assertEquals(Role.CUSTOMER, customer.getRole());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDetails customerDetails = new UserDetails("customerUser", "customerPass", "customer@example.com", "Shayan Aminaei");
        UserDetails newCustomerDetail = new UserDetails("New Name", "New Password", "new@email.com", "Shayan Aminaei");
        Customer customer = (Customer) userManager.createUser(customerDetails, Role.CUSTOMER, Role.ADMIN);
        customer = (Customer) userManager.updateUser(customer.getId(), newCustomerDetail);

        assertEquals("New Name", customer.getUsername());
        assertEquals("New Password", customer.getPassword());
        assertEquals("new@email.com", customer.getEmailString());
    }

    @Test
    void testAssignRole() throws Exception {
        UserDetails adminDetails = new UserDetails("adminUser", "adminPass", "admin@example.com", "Admin Name");
        UserDetails genDetails = new UserDetails("genericUser", "genPass", "generic@example.com", "Generic Name");
        UserDetails tellerDetails = new UserDetails("tellerUser", "tellerPass", "teller@example.com", "Teller Name");

        User admin = userManager.createUser(adminDetails, Role.ADMIN, Role.ADMIN);
        User genUser = userManager.createUser(genDetails, Role.CUSTOMER, admin.getRole());

        // Admin assigns a new role
        genUser = userManager.assignRole(genUser.getId(), Role.TELLER, admin.getRole());
        assertEquals(Role.TELLER, genUser.getRole());

        // Teller cannot assign roles (should throw an exception)
        User teller = userManager.createUser(tellerDetails, Role.TELLER, admin.getRole());
        User genUser2 = userManager.createUser(genDetails, Role.CUSTOMER, admin.getRole());

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.assignRole(genUser2.getId(), Role.CUSTOMER, teller.getRole());
        });
        assertTrue(exception.getMessage().contains("Only administrators can assign roles"));
    }
}
