package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    UserService userService = Mockito.mock(UserService.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    ItemMapper itemMapper = new ItemMapper();
    RequestService requestService;

    MockitoSession mockitoSession;

    @BeforeEach
    void startSession() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        requestService = new RequestServiceImpl(requestRepository, itemRepository, userService, itemMapper);
    }

    @AfterEach
    void finishSession() {
        mockitoSession.finishMocking();
    }

    private static ItemRequestDto dummyRequestDto = new ItemRequestDto(
            1, "desc2", 1, LocalDateTime.now(), null);

    private static ItemRequest dummyItem = new ItemRequest(1, "desc2", 1, LocalDateTime.now());

    @Test
    void shouldFailAddNewRequest() {
        dummyRequestDto.setDescription(null);

        Exception exception = assertThrows(ValidationException.class,
                () -> requestService.addNewRequest(1, dummyRequestDto));
    }

    @Test
    void shouldAddNewRequest() {
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(dummyItem);
        requestService.addNewRequest(1, dummyRequestDto);

        Mockito.verify(requestRepository).save(Mockito.any());
    }

    @Test
    void shouldFailGetRequestById() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(1, 1L));
    }
}
