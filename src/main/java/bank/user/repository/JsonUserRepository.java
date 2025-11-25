package bank.user.repository;

import bank.user.Users;

import java.util.List;

public class JsonUserRepository implements UserRepository {
    public JsonUserRepository() {
        // Load JSON automatically when repository is created
        load();
    }

    @Override
    public void load() {
        Users.load();
    }

    @Override
    public void save() {
        Users.save();
    }

    @Override
    public List<Users.User> getAll() {
        return Users.get();
    }

    @Override
    public Users.User getById(long id) {
        return Users.get(id);
    }

    @Override
    public void add(Users.User user) {
        Users.add(user);
        Users.save();
    }

    @Override
    public void update(Users.User updatedUser) {
        List<Users.User> allUsers = getAll();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).id() == updatedUser.id()) {
                allUsers.set(i, updatedUser);
                save();
                return;
            }
        }
        throw new IllegalArgumentException("User with id " + updatedUser.id() + " not found.");
    }


}