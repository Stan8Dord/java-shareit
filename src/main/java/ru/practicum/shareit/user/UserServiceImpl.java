package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Qualifier("inMemoryStorage")
    private final UserDao userRepository;

    @Autowired
    public UserServiceImpl(UserDao repository) {
        this.userRepository = repository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.createUser(validateUser(userDto, true));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        if (isUserExists(userId)) {
            User user = userRepository.updateUser(userId, validateUser(userDto, false));
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public UserDto getUserById(int userId) {
        User user = userRepository.getUserById(userId);

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users) {
            userDtoList.add(UserMapper.toUserDto(user));
        }

        return userDtoList;
    }

    public User validateUser(UserDto userDto, boolean isNew) {
        String email = userDto.getEmail();
        if (isNew && email == null) {
            log.error("Validation Error: пустой email \n" + userDto);
            throw new ValidationException("Не указан email!");
        }
        if (isEmailExists(email)) {
            log.error("Validation Error: занятый email \n" + userDto);
            throw new ConflictException("Такой email уже занят!");
        }

        return UserMapper.toUser(userDto);
    }

    @Override
    public boolean isUserExists(int userId) {
        return userRepository.getAllUsers().stream().anyMatch(user -> user.getId() == userId);
    }

    public boolean isEmailExists(String email) {
        return userRepository.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteUser(int userId) {
        if (isUserExists(userId))
            userRepository.deleteUser(userId);
        else
            throw new NotFoundException("Нет такого пользователя!");
    }
}
