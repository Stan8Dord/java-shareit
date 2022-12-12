package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingImportDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService service) {
        this.bookingService = service;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @Valid @RequestBody BookingImportDto bookingDto) {
        log.info("POST: addBooking " + bookingDto);

        return bookingService.addBooking(userId, BookingImportDto.toBookingDto(bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto replyBooking(@PathVariable("bookingId") long itemId,
                                        @RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam Boolean approved) {
        log.info("PATCH: replyBooking ");

        return bookingService.replyBooking(itemId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET: getBooking " + bookingId + " by user " + userId);

        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(
                                @Valid @RequestParam(defaultValue = "ALL") BookingState state,
                                @RequestHeader("X-Sharer-User-Id") int userId,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "32") int size) {
        log.info("GET:  getAllUserBookings by user " + userId + " state = " + state);

        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserStuffBookings(
                                @RequestParam(defaultValue = "ALL") BookingState state,
                                @RequestHeader("X-Sharer-User-Id") int ownerId,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "32") int size) {
        log.info("GET: getAllUserStuffBookings by owner " + ownerId + " state = " + state);

        return bookingService.getAllUserStuffBookings(ownerId, state, from, size);
    }
}
