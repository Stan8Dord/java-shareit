package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private LocalDateTime start = now.plusDays(1);
    private LocalDateTime end = now.plusDays(3);
    private UserDto userDto = new UserDto(6, "user1", "email1@email.com");
    private ItemDto itemDto = new ItemDto(3, "name", "description", true, 12);

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                2L,
                start,
                end,
                BookingStatus.APPROVED,
                userDto,
                itemDto);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.APPROVED.toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("email1@email.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(12);
    }
}
