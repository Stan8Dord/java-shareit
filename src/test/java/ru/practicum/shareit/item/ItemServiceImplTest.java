package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    ItemMapper mapper = new ItemMapper();
    UserService userService = Mockito.mock(UserService.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    ItemService itemService;
    MockitoSession mockitoSession;

    private static Item dummyItem1 = new Item(1, "name1", "description1", true, 1, 1);
    private static Item dummyItem2 = new Item(2, "name2", "description2", true, 1, 2);
    private static ItemDto dummyDto = new ItemDto(1, "name", "description", true, 1);
    private static final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private static Booking dummyBooking = new Booking(1, now.plusHours(-10), now.plusHours(-8), 1, 1,
            BookingStatus.WAITING);
    private static CommentDto dto = new CommentDto(1L, "comment1", "author", now);
    private static Comment comment = new Comment(1L, "comment1", 1L, 1, now);

    @BeforeEach
    void startSession() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemServiceImpl(itemRepository, mapper, commentRepository, userService, bookingRepository);
    }

    @AfterEach
    void finishSession() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldAddNewItem() {
        Mockito.doNothing().when(userService).checkUserId(1);
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(dummyItem1);

        itemService.addNewItem(1, dummyDto);

        Mockito.verify(itemRepository).save(Mockito.any());
    }

    @Test
    void shouldThrowNotFound() {
        Mockito.when(itemRepository.findByOwnerOrderByIdDesc(1))
                .thenReturn(new ArrayList<Item>(List.of(dummyItem1, dummyItem2)));

        Exception exception = assertThrows(NotFoundException.class,
                () -> itemService.modifyItem(99, dummyDto, 1));
    }

    @Test
    void shouldModifyItem() {
        Mockito.when(itemRepository.findByOwnerOrderByIdDesc(1))
                .thenReturn(new ArrayList<Item>(List.of(dummyItem1, dummyItem2)));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(dummyItem1));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(dummyItem1);

        itemService.modifyItem(1, dummyDto, 1);

        Mockito.verify(itemRepository).save(Mockito.any());
    }

    @Test
    void shouldThrowNotFoundFromGetItem() {
        Mockito.when(itemRepository.findAll()).thenReturn(new ArrayList<Item>(List.of(dummyItem1, dummyItem2)));

        Exception exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1,  99));
    }

    @Test
    void shouldFailCommentator() {
        CommentDto dto = new CommentDto(1L, "comment1", "author", LocalDateTime.now());

        Exception exception = assertThrows(ValidationException.class,
                () -> itemService.addNewComment(1, 99, dto));
    }

    @Test
    void shouldAddNewComment() {
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        Mockito.when(bookingRepository.findByBookerAndItem(anyInt(), anyLong()))
                .thenReturn(List.of(dummyBooking));
        Mockito.when(commentRepository.getUserName(anyInt())).thenReturn(List.of("author"));

        CommentDto resultDto = itemService.addNewComment(1, 1L, dto);

        assertEquals("comment1", resultDto.getText());
        assertEquals("author", resultDto.getAuthorName());
        assertEquals(now, resultDto.getCreated());
        assertEquals(1, resultDto.getId());

        Mockito.verify(commentRepository).save(Mockito.any());
        Mockito.verify(commentRepository).getUserName(anyInt());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void shouldGetItemOwnerDtoById() {
        Mockito.when(itemRepository.findAll()).thenReturn(List.of(dummyItem1));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(dummyItem1));
        Mockito.when(commentRepository.findAllByItem(anyLong())).thenReturn(List.of(comment));
        Mockito.when(commentRepository.getUserName(anyInt())).thenReturn(List.of("user"));

        itemService.getItemOwnerDtoById(1, 1L);

        Mockito.verify(itemRepository, Mockito.times(2)).findById(Mockito.any());
        Mockito.verify(commentRepository).findAllByItem(anyLong());
        Mockito.verify(commentRepository).getUserName(anyInt());
    }

    @Test
    void shouldGetItems() {
        Pageable page = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Page<Item> items = new PageImpl<>(List.of(dummyItem1));
        Mockito.when(itemRepository.findByOwner(1, page)).thenReturn(items);

        itemService.getItems(1, 0, 3);

        Mockito.verify(itemRepository, Mockito.times(1)).findByOwner(anyInt(), Mockito.any());
    }
}
