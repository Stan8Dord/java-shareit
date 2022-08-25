package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(int userId, UserDto userDto);

    UserDto getUserById(int id);

    List<UserDto> getAllUsers();

    void deleteUser(int userId);

    boolean isUserExists(int userId);
}
