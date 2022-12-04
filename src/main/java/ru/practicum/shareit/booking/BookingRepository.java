package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Page<Booking> findByBookerOrderByStartDesc(int bookerId, Pageable pageable);

    Page<Booking> findByBookerAndStatusOrderByStartDesc(int bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime currentDateTime, Pageable page);

    Page<Booking> findByBookerAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime currentDateTime, Pageable page);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "where b.booker = ?1 " +
            "and (?2 between b.start and b.end) " +
            "order by b.start desc")
    Page<Booking> findByBookerCurrentBookings(int bookerId, LocalDateTime currentDateTime, Pageable pageable);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b inner join Item as it on b.item = it.id " +
            "where it.owner = ?1 " +
            "order by b.start desc")
    Page<Booking> getBookingsByUserItems(int userId, Pageable pageable);

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
