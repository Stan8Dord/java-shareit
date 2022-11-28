package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(int userId, BookingDto bookingDto);

    BookingDto replyBooking(long bookingId, int userId, Boolean approved);

    BookingDto getBooking(long bookingId, int userId);

    List<BookingDto> getAllUserBookings(int userId, BookingState state);

    List<BookingDto> getAllUserStuffBookings(int ownerId, BookingState state);
}
