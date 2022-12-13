package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(int userId, ItemDto itemDto);

    ItemDto modifyItem(long itemId, ItemDto itemDto, int userId);

    ItemDto getItemById(int userId, long itemId);

    ItemOwnerDto getItemOwnerDtoById(int userId, long itemId);

    List<ItemOwnerDto> getItems(int userId, int from, int size);

    List<ItemDto> searchItems(int userId, String text, int from, int size);

    Boolean isItemAvailable(long itemId);

    boolean isItemOwner(long itemId, int userId);

    CommentDto addNewComment(int userId, long itemId, CommentDto dto);
}
