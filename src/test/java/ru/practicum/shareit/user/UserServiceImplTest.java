package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserServiceImpl userService;
    private static final User dummyUser1 = new User(1, "user1", "email1@email.com");
    private static final User dummyUser2 = new User(2, "user2", "email1@email.com");
    private static final UserDto dummyDtoFailEmail = new UserDto(1, "user1", null);
    private static final UserDto dummyDto1 = new UserDto(1, "user1", "email1@email.com");
    private MockitoSession mockitoSession;

    @BeforeEach
    void startSession() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void finishSession() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldFailUpdateUser() {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<User>(List.of(dummyUser2)));

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(1, new UserDto(1, "name", "email@email.com")));
    }

    @Test
    void shouldSuccessfullyUpdateUser() {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<User>(List.of(dummyUser1)));
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(dummyUser1));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(dummyUser1);

        userService.updateUser(1, new UserDto(1, "newName", "newEmail@email.com"));

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void shouldFailUserValidation() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(dummyUser1);

        Exception exception = assertThrows(ValidationException.class,
                () -> userService.createUser(dummyDtoFailEmail));
    }

    @Test
    void shouldCreateUser() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(dummyUser1);

        userService.createUser(dummyDto1);

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void shouldGetUserById() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(dummyUser1));

        userService.getUserById(1);

        Mockito.verify(userRepository).findById(1);
    }

    @Test
    void shouldFailGetUserById() {
        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(99));
    }

    @Test
    void shouldDeleteUserById() {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<User>(List.of(dummyUser1)));
        Mockito.doNothing().when(userRepository).deleteById(1);

        userService.deleteUser(1);
        Mockito.verify(userRepository).deleteById(1);
    }
}
