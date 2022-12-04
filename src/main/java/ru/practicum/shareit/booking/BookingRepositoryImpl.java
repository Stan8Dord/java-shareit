package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    private final BookingRepository bookingRepository;

    public BookingRepositoryImpl(@Lazy BookingRepository repository) {
        this.bookingRepository = repository;
    }

    @Override
    public List<Booking> getBookingsByUserItemsWithState(int ownerId, BookingState state, int from, int size) {
        List<Booking> bookingList = new ArrayList<>();
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = bookingRepository.getBookingsByUserItems(ownerId, page);

        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookingList = bookings.stream().collect(Collectors.toList());
                break;
            case REJECTED:
                bookingList = bookings.stream().filter(dto -> dto.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingList = bookings.stream().filter(dto -> dto.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingList = bookings.stream().filter(dto -> dto.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingList = bookings.stream().filter(dto -> dto.getStart().isBefore(now))
                        .filter(dto -> dto.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingList = bookings.stream().filter(dto -> dto.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
        }
        return bookingList;
    }
}
