package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryStorage")
public class UserDaoImplInMemory implements UserDao {
    private final Map<Integer, User> users = new HashMap<>();
    private static Integer userId = 1;

    @Override
    public User createUser(User user) {
        user.setId(userId);
        users.put(userId++, user);

        return user;
    }

    @Override
    public User updateUser(int id, User patchUser) {
        User user = users.get(id);

        if (patchUser.getName() != null)
            user.setName(patchUser.getName());
        if (patchUser.getEmail() != null)
            user.setEmail(patchUser.getEmail());

        users.put(id, user);

        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
