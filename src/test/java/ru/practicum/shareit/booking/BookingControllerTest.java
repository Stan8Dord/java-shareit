package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingImportDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private LocalDateTime start = now.plusDays(1);
    private LocalDateTime end = now.plusDays(3);
    private BookingImportDto bookingDto = new BookingImportDto(1L, start, end);
    private UserDto dummyUser = new UserDto(
            1,
            "user1",
            "email1@email.com");
    ItemDto itemDto = new ItemDto(
            1L,
            "name",
            "description",
            true,
            12);
    private BookingDto resultBookingDto = new BookingDto(
            1L,
            1L,
            start,
            end,
            BookingStatus.WAITING,
            dummyUser,
            itemDto);
    private BookingDto resultBookingDto2 = new BookingDto(
            1L,
            1L,
            start,
            end,
            BookingStatus.APPROVED,
            null,
            null);
    List<BookingDto> bookingDtoList = new ArrayList<>(List.of(resultBookingDto2));


    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(anyInt(), any())).thenReturn(resultBookingDto);

        mvc.perform(post("/bookings")
                    .content(mapper.writeValueAsString(bookingDto))
                    .header("X-Sharer-User-Id", 1)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is("user1")))
                .andExpect(jsonPath("$.booker.email", is(dummyUser.getEmail())))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("name")))
                .andExpect(jsonPath("$.item.description", is("description")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId", is(12)));
    }

    @Test
    void replyBookingTest() throws  Exception {
        when(bookingService.replyBooking(anyLong(), anyInt(), anyBoolean())).thenReturn(resultBookingDto2);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getBookingTest() throws  Exception {
        when(bookingService.getBooking(anyLong(), anyInt())).thenReturn(resultBookingDto2);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getAllUserBookingsTest() throws  Exception {
        when(bookingService.getAllUserBookings(anyInt(), any(), anyInt(), anyInt())).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].itemId", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getAllUserStuffBookingsTest() throws  Exception {
        when(bookingService.getAllUserStuffBookings(anyInt(), any(), anyInt(), anyInt())).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].itemId", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void shouldFailGetBookingsWithWrongState() throws Exception {
        mvc.perform(get("/bookings/owner?state=UNSUPPORTED_STATUS")
                            .header("X-Sharer-User-Id", 1)
                            .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }
}
