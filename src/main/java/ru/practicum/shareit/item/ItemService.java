package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto addNewItem(int userId, ItemDto itemDto);

    ItemDto modifyItem(long itemId, ItemDto itemDto, int userId);

    ItemDto getItemById(int userId, long itemId);

    List<ItemDto> getItems(int userId);

    List<ItemDto> searchItems(int userId, String text);
}
