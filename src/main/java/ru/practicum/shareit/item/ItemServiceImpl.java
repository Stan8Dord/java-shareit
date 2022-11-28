package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper mapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper mapper, CommentRepository commentRepository,
                           UserService userService, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addNewItem(int userId, ItemDto itemDto) {
        userService.checkUserId(userId);

        Item item = itemRepository.save(mapper.toItem(userId, itemDto));

        return mapper.toItemDto(item);
    }

    @Override
    public ItemDto modifyItem(long itemId, ItemDto itemDto, int userId) {
        userService.checkUserId(userId);
        checkItemToModify(itemId, userId);
        Item item = itemRepository.findById(itemId).get();
        String name = itemDto.getName();
        if (name != null)
            item.setName(name);
        String description = itemDto.getDescription();
        if (description != null)
            item.setDescription(description);
        Boolean isAvailable = itemDto.getAvailable();
        if (isAvailable != null)
            item.setIsAvailable(isAvailable);

        item = itemRepository.save(item);

        return mapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(int userId, long itemId) {
        userService.checkUserId(userId);
        checkItemId(itemId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        return mapper.toItemDto(itemOptional.get());
    }

    @Override
    public ItemOwnerDto getItemOwnerDtoById(int userId, long itemId) {
        userService.checkUserId(userId);
        checkItemId(itemId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        LocalDateTime now = LocalDateTime.now();
        BookingShort nextBooking = null;
        BookingShort lastBooking = null;
        if (isItemOwner(itemId, userId)) {
            nextBooking = bookingRepository
                    .getBookingShortFutureByItem(itemId, now).stream().findFirst().orElse(null);
            lastBooking = bookingRepository
                    .getBookingShortPastByItem(itemId, now).stream().findFirst().orElse(null);
        }
        List<CommentDto> commentDtos = commentRepository.findAllByItem(itemId).stream()
                .map(comment -> CommentMapper.toCommentDto(
                        comment, commentRepository.getUserName(comment.getAuthor()).get(0))
                    ).collect(Collectors.toList());

        return mapper.toItemOwnerDto(itemOptional.get(), lastBooking, nextBooking, commentDtos);
    }

    @Override
    public List<ItemOwnerDto> getItems(int userId) {
        userService.checkUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwnerOrderByIdDesc(userId);
        List<ItemOwnerDto> itemDtoList = new ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            long itemId = items.get(i).getId();
            BookingShort nextBooking = bookingRepository
                    .getBookingShortFutureByItem(itemId, now).stream().findFirst().orElse(null);
            BookingShort lastBooking = bookingRepository
                    .getBookingShortPastByItem(itemId, now).stream().findFirst().orElse(null);
            List<CommentDto> commentDtos = commentRepository.findAllByItem(itemId).stream()
                    .map(comment -> CommentMapper.toCommentDto(
                            comment, commentRepository.getUserName(comment.getAuthor()).get(0))
                        ).collect(Collectors.toList());
            itemDtoList.add(mapper.toItemOwnerDto(items.get(i), lastBooking, nextBooking, commentDtos));
        }

        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(int userId, String text) {
        List<ItemDto> itemDtoList = itemRepository.findByTextOnlyAvailable(text);

        return itemDtoList;
    }

    private void checkItemId(long itemId) {
        if (itemRepository.findAll().stream().noneMatch(item -> item.getId() == itemId))
            throw new NotFoundException("Вещь не найдена!");
    }

    private void checkItemToModify(long itemId, int userId) {
        if (itemRepository.findByOwnerOrderByIdDesc(userId).stream().noneMatch(item -> item.getId() == itemId))
            throw new NotFoundException("Вещь не найдена!");
    }

    @Override
    public Boolean isItemAvailable(long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            throw new NotFoundException("Вещь не найдена!");

        return itemOptional.get().getIsAvailable();
    }

    @Override
    public boolean isItemOwner(long itemId, int userId) {
        checkItemId(itemId);
        if (itemRepository.findById(itemId).get().getOwner() == userId)
            return true;
        else
            return false;
    }

    @Override
    public CommentDto addNewComment(int userId, long itemId, CommentDto dto) {
        checkCommentator(userId, itemId, dto.getText());

        Comment comment = commentRepository.save(new Comment(dto.getText(), itemId, userId, LocalDateTime.now()));
        String authorName = commentRepository.getUserName(userId).get(0);

        return CommentMapper.toCommentDto(comment, authorName);
    }

    private void checkCommentator(int userId, long itemId, String text) {
        List<Booking> bookings = bookingRepository.findByBookerAndItem(userId, itemId);

        if (!bookings.stream().filter(dto -> dto.getEnd().isBefore(LocalDateTime.now()))
                .findFirst().isPresent() || text.equals(""))
            throw new ValidationException("Фантазер ;)");
    }
}
