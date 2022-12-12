package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    UserService userService = Mockito.mock(UserService.class);
    ItemService itemService = Mockito.mock(ItemService.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    BookingService bookingService;

    MockitoSession mockitoSession;

    private static final LocalDateTime now = LocalDateTime.now();
    private static BookingDto bookingDtoWrongPeriod = new BookingDto(1,
            now.plusHours(1), now.plusHours(-8));
    private static BookingDto dummyBookingDto1 = new BookingDto(1,
            now.plusHours(1), now.plusHours(8));
    private static ItemDto dummyItemDto = new ItemDto(1, "name", "description", true, 1);
    private static UserDto dummyUserDto = new UserDto(1, "user1", "email1@email.com");
    private static Booking dummyBooking = new Booking(1, now, now.plusHours(8), 1, 1,
            BookingStatus.WAITING);
    private static Booking dummyBooking2 = new Booking(2, now, now.plusHours(3), 1, 2,
            BookingStatus.WAITING);

    @BeforeEach
    void startSession() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
    }

    @AfterEach
    void finishSession() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldFailDatesAddBooking() {
        Mockito.doNothing().when(userService).checkUserId(1);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1, bookingDtoWrongPeriod));
    }

    @Test
    void shouldFailOwnerAddBooking() {
        Mockito.doNothing().when(userService).checkUserId(1);
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(true);

        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1, dummyBookingDto1));
    }

    @Test
    void shouldFailNotAvailableAddBooking() {
        Mockito.doNothing().when(userService).checkUserId(1);
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(false);
        Mockito.when(itemService.isItemAvailable(1)).thenReturn(false);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1, dummyBookingDto1));
    }

    @Test
    void shouldAddBooking() {
        Mockito.doNothing().when(userService).checkUserId(1);
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(false);
        Mockito.when(itemService.isItemAvailable(1)).thenReturn(true);
        Mockito.when(itemService.getItemById(Mockito.anyInt(), Mockito.anyInt())).thenReturn(dummyItemDto);
        Mockito.when(userService.getUserById(1)).thenReturn(dummyUserDto);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(dummyBooking);

        bookingService.addBooking(1, dummyBookingDto1);

        Mockito.verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void shouldCheckBooking() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingService.replyBooking(1L, 1, true));
    }

    @Test
    void shouldCheckItemOwner() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(dummyBooking));
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingService.replyBooking(1L, 1, true));
    }

    @Test
    void shouldCheckApproved() {
        dummyBooking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(dummyBooking));
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(true);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingService.replyBooking(1L, 1, true));
    }

    @Test
    void shouldReplyBooking() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(dummyBooking));
        Mockito.when(itemService.isItemOwner(1, 1)).thenReturn(true);
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(dummyBooking);

        bookingService.replyBooking(1, 1, false);

        Mockito.verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void shouldSwitchStateUserBookings() {
        Page<Booking> bookings = new PageImpl<>(List.of(dummyBooking, dummyBooking2));
        Mockito.when(bookingRepository.findByBookerOrderByStartDesc(Mockito.anyInt(),
                Mockito.any())).thenReturn(bookings);
        Mockito.when(bookingRepository.findByBookerAndStatusOrderByStartDesc(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(bookings);
        Mockito.when(bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(bookings);
        Mockito.when(bookingRepository.findByBookerCurrentBookings(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(bookings);
        Mockito.when(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(bookings);

        bookingService.getAllUserBookings(1, BookingState.ALL, 0, 3);
        Mockito.verify(bookingRepository).findByBookerOrderByStartDesc(Mockito.anyInt(), Mockito.any());
        bookingService.getAllUserBookings(1, BookingState.REJECTED, 0, 3);
        bookingService.getAllUserBookings(1, BookingState.WAITING, 0, 3);
        Mockito.verify(bookingRepository,Mockito.times(2))
                .findByBookerAndStatusOrderByStartDesc(Mockito.anyInt(), Mockito.any(), Mockito.any());
        bookingService.getAllUserBookings(1, BookingState.PAST, 0, 3);
        Mockito.verify(bookingRepository)
                .findByBookerAndEndBeforeOrderByStartDesc(Mockito.anyInt(), Mockito.any(), Mockito.any());
        bookingService.getAllUserBookings(1, BookingState.CURRENT, 0, 3);
        Mockito.verify(bookingRepository)
                .findByBookerCurrentBookings(Mockito.anyInt(), Mockito.any(), Mockito.any());
        bookingService.getAllUserBookings(1, BookingState.FUTURE, 0, 3);
        Mockito.verify(bookingRepository)
                .findByBookerAndStartAfterOrderByStartDesc(Mockito.anyInt(), Mockito.any(), Mockito.any());
    }
}
