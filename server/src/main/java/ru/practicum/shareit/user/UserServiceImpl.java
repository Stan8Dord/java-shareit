package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(validateUser(userDto, true));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        if (isUserExists(userId)) {
            User user = userRepository.findById(userId).get();
            String name = userDto.getName();
            if (name != null)
                user.setName(name);
            String email = userDto.getEmail();
            if (email != null)
                user.setEmail(email);
            return UserMapper.toUserDto(userRepository.save(user));
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public UserDto getUserById(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent())
            return UserMapper.toUserDto(userOptional.get());
        else {
            log.error("Error: не найден пользователь " + userId + "\n");
            throw new NotFoundException("Error: не найден пользователь " + userId);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
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

        return UserMapper.toUser(userDto);
    }

    private boolean isUserExists(int userId) {
        return userRepository.findAll().stream().anyMatch(user -> user.getId() == userId);
    }

    @Override
    public void deleteUser(int userId) {
        if (isUserExists(userId))
            userRepository.deleteById(userId);
        else
            throw new NotFoundException("Нет такого пользователя!");
    }

    @Override
    public void checkUserId(int userId) {
        if (!isUserExists(userId))
            throw new NotFoundException("Пользователь не найден!");
    }
}
