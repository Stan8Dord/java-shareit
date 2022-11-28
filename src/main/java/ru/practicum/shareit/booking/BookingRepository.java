package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    List<Booking> findByBookerOrderByStartDesc(int bookerId);

    List<Booking> findByBookerAndStatusOrderByStartDesc(int bookerId, BookingStatus status);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime currentDateTime);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime currentDateTime);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.booker = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findByBookerCurrentBookings(int bookerId, LocalDateTime currentDateTime);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b inner join Item as it on b.item = it.id " +
            "where it.owner = ?1 " +
            "order by b.start desc")
    List<Booking> getBookingsByUserItems(int userId);

    //List<Booking> findByItemOrderByStartDateDesc(Long itemId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker) " +
            "from Booking as b " +
            "where b.item = ?1 " +
            "and b.end < ?2 " +
            "order by b.end desc")
    List<BookingShort> getBookingShortPastByItem(long itemId, LocalDateTime time);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker) " +
            "from Booking as b " +
            "where b.item = ?1 " +
            "and b.start > ?2 " +
            "order by b.end desc")
    List<BookingShort> getBookingShortFutureByItem(long itemId, LocalDateTime time);

    List<Booking> findByBookerAndItem(int bookerId, long itemId);
}
