package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingRepositoryCustom {
    List<Booking> getBookingsByUserItemsWithState(int userId, BookingState state);
}
