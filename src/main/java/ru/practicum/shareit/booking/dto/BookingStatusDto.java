package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

@Data
@AllArgsConstructor
public class BookingStatusDto {
    private BookingStatus status;
}
