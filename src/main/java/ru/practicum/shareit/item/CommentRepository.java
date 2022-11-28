package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select u.name " +
            "from User as u " +
            " where u.id = ?1")
    List<String> getUserName(int userId);

    List<Comment> findAllByItem(long itemId);
}
