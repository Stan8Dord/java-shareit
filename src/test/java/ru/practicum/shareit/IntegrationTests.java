package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {
    private final UserRepository userRepository;
    private final UserService userService;
    private static User dummyUser1 = new User("user1", "email1@email.com");
    private static User dummyUser2 = new User("user2", "email2@email.com");
    private static UserDto dummyDto = new UserDto(3, "newName", "newEmail@email.com");

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    @Order(1)
    void shouldGetAllUsers() {
        userRepository.save(dummyUser1);
        userRepository.save(dummyUser2);

        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size(), equalTo(2));
        assertThat(userDtoList.get(1).getEmail(), equalTo("email2@email.com"));
        assertThat(userDtoList.get(0).getName(), equalTo(("user1")));
    }

    @Test
    @Order(2)
    void shouldUpdateUser() {
        userRepository.save(dummyUser1);

        UserDto userDto = userService.updateUser(3, dummyDto);

        assertThat(userDto.getEmail(), equalTo("newEmail@email.com"));
        assertThat(userDto.getName(), equalTo(("newName")));
    }

}
