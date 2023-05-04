package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingImportDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    public static BookingDto toBookingDto(BookingImportDto booking) {
        return new BookingDto(
                0,
                booking.getItemId(),
                booking.getStart(),
                booking.getEnd(),
                null,
                null,
                null);
    }
}
