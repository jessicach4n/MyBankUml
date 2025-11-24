package bank.user.repository;

import bank.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public void save(User user) {
        storage.put(user.getId(), user);
    }

    @Override
    public User findById(long id) {
        return storage.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }
}