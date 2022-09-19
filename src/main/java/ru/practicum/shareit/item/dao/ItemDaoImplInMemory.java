package ru.practicum.shareit.item.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemDao")
public class ItemDaoImplInMemory implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long itemId = 1L;
    private final UserDao userRepository;

    @Autowired
    public ItemDaoImplInMemory(UserDao userRepostory) {
        this.userRepository = userRepostory;
    }

    @Override
    public Item addNewItem(int userId, Item item) {
        item.setId(itemId);
        item.setOwner(userRepository.getUserById(userId));

        items.put(itemId++, item);

        return item;
    }

    @Override
    public Item modifyItem(long itemId, ItemDto modItem, int userId) {
        Item item = items.get(itemId);

        if (modItem.getName() != null)
            item.setName(modItem.getName());
        if (modItem.getDescription() != null)
            item.setDescription(modItem.getDescription());
        if (modItem.getAvailable() != null)
            item.setAvailable(modItem.getAvailable());

        items.put(itemId, item);

        return item;
    }

    @Override
    public Item getItemById(int userId, long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems(int userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(int userId, String text) {
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
