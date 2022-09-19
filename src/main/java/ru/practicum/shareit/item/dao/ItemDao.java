package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemDao {
    Item addNewItem(int userId, Item item);

    Item modifyItem(long itemId, ItemDto itemDto, int userId);

    Item getItemById(int userId, long itemId);

    List<Item> getItems(int userId);

    List<Item> searchItems(int userId, String text);
}
