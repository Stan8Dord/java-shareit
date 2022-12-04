package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerOrderByIdDesc(int ownerId);

    Page<Item> findByOwner(int ownerId, Pageable pageable);

    @Query("select new " +
            "ru.practicum.shareit.item.dto.ItemDto(it.id, it.name, it.description, it.isAvailable, it.request) " +
            "from Item as it " +
            "where it.owner = ?1 " +
            "and (lower(it.name) like lower(concat('%', ?2, '%')) " +
            "or lower(it.description) like lower(concat('%', ?2, '%'))) " +
            "order by it.id")
    List<ItemDto> findByUserIdAndText(int userId, String text);

    @Query("select new " +
            "ru.practicum.shareit.item.dto.ItemDto(it.id, it.name, it.description, it.isAvailable, it.request) " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', ?1, '%')) " +
            "or lower(it.description) like lower(concat('%', ?1, '%'))) " +
            "and it.isAvailable = TRUE " +
            "order by it.id")
    Page<ItemDto> findByTextOnlyAvailable(String text, Pageable pageable);

    List<Item> findAllByRequestOrderByIdDesc(long requestId);
}
