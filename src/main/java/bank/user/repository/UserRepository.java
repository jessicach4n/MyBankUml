package bank.user.repository;

import bank.user.Users;
import java.util.List;

public interface UserRepository {
    void load();                  // Load users.json → memory
    void save();                  // Save all memory → users.json
    List<Users.User> getAll();    // Get all users
    Users.User getById(long id);  // Get specific user
    void add(Users.User user);    // Add a new user
    void update(Users.User user);
}
