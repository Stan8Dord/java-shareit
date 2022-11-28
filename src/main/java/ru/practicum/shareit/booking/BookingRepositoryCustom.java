package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingRepositoryCustom {
    List<Booking> getBookingsByUserItemsWithState(int userId, BookingState state);
}
