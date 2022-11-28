package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    private final BookingRepository bookingRepository;

    public BookingRepositoryImpl(@Lazy BookingRepository repository) {
        this.bookingRepository = repository;
    }

    @Override
    public List<Booking> getBookingsByUserItemsWithState(int ownerId, BookingState state) {
        List<Booking> bookings = bookingRepository.getBookingsByUserItems(ownerId);
        System.out.println("tut: " + bookings); //ssdflk
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookings;
            case REJECTED:
                bookings = bookings.stream().filter(dto -> dto.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookings.stream().filter(dto -> dto.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookings.stream().filter(dto -> dto.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookings.stream().filter(dto -> dto.getStart().isBefore(now))
                        .filter(dto -> dto.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookings.stream().filter(dto -> dto.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
        }
        return bookings;
    }
}
