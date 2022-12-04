package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(int userId, BookingDto bookingDto);

    BookingDto replyBooking(long bookingId, int userId, Boolean approved);

    BookingDto getBooking(long bookingId, int userId);

    List<BookingDto> getAllUserBookings(int userId, BookingState state, int from, int size);

    List<BookingDto> getAllUserStuffBookings(int ownerId, BookingState state, int from, int size);
}
