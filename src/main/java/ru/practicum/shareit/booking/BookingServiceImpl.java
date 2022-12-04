package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository repository,
                              UserService userService, ItemService itemService) {
        this.bookingRepository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto addBooking(int userId, BookingDto bookingDto) {
        Booking booking = null;
        userService.checkUserId(userId);
        checkBookingDates(bookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (itemService.isItemOwner(bookingDto.getItemId(), userId))
            throw new NotFoundException("Вещь не доступна для бронирования владельцем!");
        else if (itemService.isItemAvailable(bookingDto.getItemId())) {
            booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, userId));
        } else {
            throw new ValidationException("Вещь не доступна для бронирования!");
        }

        return createBookingDto(userId, booking);
    }

    @Override
    public BookingDto replyBooking(long bookingId, int ownerId, Boolean approved) {
        Booking booking = checkBooking(bookingId);
        checkItemOwner(booking, ownerId);
        checkApproved(booking.getStatus());

        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);

        return createBookingDto(booking.getBooker(), booking);
    }

    @Override
    public BookingDto getBooking(long bookingId, int userId) {
        Booking booking = checkBooking(bookingId);
        checkBookingOrItemOwner(booking, userId);

        return createBookingDto(booking.getBooker(), booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllUserBookings(int userId, BookingState state, int fromElement, int size) {
        userService.checkUserId(userId);
        Page<Booking> bookings = null;
        int from = fromElement >= 0 ? fromElement / size : fromElement;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerOrderByStartDesc(userId, page);
                System.out.println("Сработало ALL");
                break;
            case REJECTED:
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(userId,
                        BookingStatus.valueOf(state.toString()), page);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByBookerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings =  bookingRepository.findByBookerCurrentBookings(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findByBookerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
        }

        System.out.println("pages: " + bookings.toString());
        List<BookingDto> dtos = bookings.stream().map(booking -> createBookingDto(userId, booking)).collect(Collectors.toList());
        System.out.println("Dtos: " + dtos.toString());

        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllUserStuffBookings(int ownerId, BookingState state, int fromElement, int size) {
        userService.checkUserId(ownerId);
        int from = fromElement >= 0 ? fromElement / size : fromElement;
        List<Booking> bookings = bookingRepository.getBookingsByUserItemsWithState(ownerId, state, from , size);
        return bookings.stream().map(booking -> createBookingDto(booking.getBooker(), booking))
                .collect(Collectors.toList());
    }

    private Booking checkBooking(long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent())
            return bookingOptional.get();
        else
            throw new NotFoundException("Booking " + bookingId + " не найдено!");
    }

    private BookingDto createBookingDto(int userId, Booking booking) {
        ItemDto itemDto = itemService.getItemById(userId, booking.getItem());
        UserDto userDto = userService.getUserById(userId);

        return BookingMapper.toBookingDto(booking, itemDto, userDto);
    }

    private void checkBookingDates(BookingDto booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (end.isBefore(now) || end.isBefore(start) || start.isBefore(now))
            throw new ValidationException("Некорректное время аренды!");
    }

    private Boolean checkBookingOrItemOwner(Booking booking, int userId) {
        if (booking.getBooker() == userId || checkItemOwner(booking, userId))
            return true;
        else
            throw new NotFoundException("Соответствующая аренда не найдена!");
    }

    private Boolean checkItemOwner(Booking booking, int userId) {
        if (itemService.isItemOwner(booking.getItem(), userId))
            return true;
        else
            throw new NotFoundException("Соответствующая аренда не найдена!");
    }

    private void checkApproved(BookingStatus status) {
        if (status.toString().equals("APPROVED"))
            throw new ValidationException("Заявка уже подтверждена!");
    }
}
