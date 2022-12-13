package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, int bookerId) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                bookerId,
                bookingDto.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        return new BookingDto(
                booking.getId(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userDto,
                itemDto);
    }
}
