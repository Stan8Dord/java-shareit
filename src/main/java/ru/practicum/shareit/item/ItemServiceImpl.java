package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Qualifier("InMemoryItemDao")
    private final ItemDao itemRepository;
    private final ItemMapper mapper;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemDao itemRepository, ItemMapper mapper, UserService userService) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    public ItemDto addNewItem(int userId, ItemDto itemDto) {
        checkUserId(userId);
        Item item = itemRepository.addNewItem(userId, mapper.toItem(userId, itemDto));

        return mapper.toItemDto(item);
    }

    @Override
    public ItemDto modifyItem(long itemId, ItemDto itemDto, int userId) {
        checkUserId(userId);
        checkItemId(itemId, userId);
        Item item = itemRepository.modifyItem(itemId, itemDto, userId);

        return mapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(int userId, long itemId) {
        checkUserId(userId);
        Item item = itemRepository.getItemById(userId, itemId);

        return mapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        checkUserId(userId);
        List<Item> items = itemRepository.getItems(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : items) {
            itemDtoList.add(mapper.toItemDto(item));
        }

        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(int userId, String text) {
        List<Item> items = itemRepository.searchItems(userId, text);
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : items) {
            itemDtoList.add(mapper.toItemDto(item));
        }

        return itemDtoList;
    }

    private void checkUserId(int userId) {
        if (!userService.isUserExists(userId))
            throw new NotFoundException("Пользователь не найден!");
    }

    private void checkItemId(long itemId, int userId) {
        if (itemRepository.getItems(userId).stream().noneMatch(item -> item.getId() == itemId))
            throw new NotFoundException("Вещь не найдена!");
    }
}
