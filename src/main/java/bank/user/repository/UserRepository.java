package bank.user.repository;

import bank.user.User;

import java.util.List;

public interface UserRepository {
    void save(User user);
    User findById(long id);
    List<User> findAll();
}
