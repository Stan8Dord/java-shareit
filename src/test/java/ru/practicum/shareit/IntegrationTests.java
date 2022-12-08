package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
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
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private static final LocalDateTime now = LocalDateTime.now();
    private User dummyUser1 = new User("user1", "email1@email.com");
    private User dummyUser2 = new User("user2", "email2@email.com");
    private UserDto dummyDto = new UserDto(3, "newName", "newEmail@email.com");
    private Item dummyItem1 = new Item("name1", "description1", true, 4, 1);
    private Booking dummyBooking1 = new Booking(now.plusDays(-3), now.plusDays(-2), 1, 4, BookingStatus.APPROVED);

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

    @Test
    @Order(3)
    void shouldGetAllUserStuffBookings() {
        userRepository.save(dummyUser1);
        System.out.println("tut = " + dummyItem1);
        System.out.println(userRepository.findAll());
        itemRepository.save(dummyItem1);
        bookingRepository.save(dummyBooking1);

        List<BookingDto> bookingDtoList = bookingService.getAllUserStuffBookings(4, BookingState.ALL, 0, 1);

        assertThat(bookingDtoList.size(), equalTo(1));
        BookingDto bookingDto = bookingDtoList.get(0);
        assertThat(bookingDto.getBooker().getName(), equalTo("user1"));
        assertThat(bookingDto.getItem().getDescription(), equalTo("description1"));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }
}
