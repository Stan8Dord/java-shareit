package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST: item " + itemDto);

        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto modifyItem(@PathVariable("itemId") long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("PATCH: item " + itemDto);

        return itemService.modifyItem(itemId, itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable long itemId,
                                    @RequestBody CommentDto dto) {
        log.info("POST: addNewComment " + dto.getText());

        return itemService.addNewComment(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") long itemId) {
        log.info("GET: itemId " + itemId);

        return itemService.getItemOwnerDtoById(userId, itemId);
    }

    @GetMapping
    public List<ItemOwnerDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "32") int size) {
        log.info("GET: allItems by userId " + userId);

        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "32") int size) {
        log.info("GET: search text " + text);

        return text.equals("") ? new ArrayList<>() : itemService.searchItems(userId, text, from, size);
    }
}
