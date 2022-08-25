package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(int id, User user);

    User getUserById(int id);

    List<User> getAllUsers();

    void deleteUser(int id);
}
