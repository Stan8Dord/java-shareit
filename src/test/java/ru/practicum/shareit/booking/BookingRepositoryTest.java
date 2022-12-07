package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private final static LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    private Booking dummyBooking = new Booking(now.plusDays(-1), now.plusDays(1), 1, 1, BookingStatus.APPROVED);
    private Booking dummyBooking1 = new Booking(now.plusDays(-3), now.plusDays(-2), 1, 1, BookingStatus.APPROVED);
    private Booking dummyBooking3 = new Booking(now.plusDays(2), now.plusDays(3), 1, 1, BookingStatus.APPROVED);
    private User dummyUser1 = new User(1, "user1", "email1@email.com");
    private Item dummyItem1 = new Item(1, "name1", "description1", true, 1, 1);
    private Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"));

    @BeforeEach
    void beforeEach() {
        userRepository.save(dummyUser1);
        itemRepository.save(dummyItem1);
        bookingRepository.save(dummyBooking1);
        bookingRepository.save(dummyBooking);
        bookingRepository.save(dummyBooking3);
    }

    @Test
    void findByBookerCurrentBookingsTest() {
        Page<Booking> bookings = bookingRepository.findByBookerCurrentBookings(1, now, page);
        Optional<Booking> bookingOptional = bookings.stream().findFirst();
        Assertions.assertTrue(bookingOptional.isPresent());
        Booking booking = bookingOptional.get();
        Assertions.assertEquals(dummyBooking.getBooker(), booking.getBooker());
        Assertions.assertEquals(dummyBooking.getItem(), booking.getItem());
        Assertions.assertEquals(dummyBooking.getStart(), booking.getStart());
        Assertions.assertEquals(dummyBooking.getStatus(), booking.getStatus());
    }

    @Test
    void getBookingsByUserItemsTest() {
        Page<Booking> bookings = bookingRepository.getBookingsByUserItems(1, page);

        Optional<Booking> bookingOptional = bookings.stream().findFirst();
        Assertions.assertTrue(bookingOptional.isPresent());
        Booking booking = bookingOptional.get();
        Assertions.assertEquals(dummyBooking3.getBooker(), booking.getBooker());
        Assertions.assertEquals(dummyBooking3.getItem(), booking.getItem());
        Assertions.assertEquals(dummyBooking3.getStart(), booking.getStart());
        Assertions.assertEquals(dummyBooking3.getStatus(), booking.getStatus());
    }

    @Test
    void getBookingShortPastByItemTest() {
        List<BookingShort> bookings = bookingRepository.getBookingShortPastByItem(1, now);

        Optional<BookingShort> bookingOptional = bookings.stream().findFirst();
        Assertions.assertTrue(bookingOptional.isPresent());
        BookingShort booking = bookingOptional.get();
        Assertions.assertEquals(1, booking.getBookerId());
        Assertions.assertEquals(1, booking.getId());
    }

    @Test
    void getBookingShortFutureByItemTest() {
        List<BookingShort> bookings = bookingRepository.getBookingShortFutureByItem(1, now);

        Optional<BookingShort> bookingOptional = bookings.stream().findFirst();
        Assertions.assertTrue(bookingOptional.isPresent());
        BookingShort booking = bookingOptional.get();
        Assertions.assertEquals(1, booking.getBookerId());
        Assertions.assertEquals(3, booking.getId());
    }

    @Test
    void getBookingsByUserItemsWithStateTest() {
        List<Booking> bookings = bookingRepository.getBookingsByUserItemsWithState(1, BookingState.FUTURE, 0, 1);

        Optional<Booking> bookingOptional = bookings.stream().findFirst();
        Assertions.assertTrue(bookingOptional.isPresent());
        Booking booking = bookingOptional.get();
        Assertions.assertEquals(dummyBooking3.getBooker(), booking.getBooker());
        Assertions.assertEquals(dummyBooking3.getItem(), booking.getItem());
        Assertions.assertEquals(dummyBooking3.getStart(), booking.getStart());
        Assertions.assertEquals(dummyBooking3.getStatus(), booking.getStatus());
    }
}
