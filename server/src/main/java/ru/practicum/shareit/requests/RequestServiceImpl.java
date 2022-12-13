package ru.practicum.shareit.requests;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository,
                              UserService userService, ItemMapper itemMapper) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemRequestDto addNewRequest(int userId, ItemRequestDto dto) {
        ItemRequest itemRequest;
        userService.checkUserId(userId);

        if (dto.getDescription() == null)
            throw new ValidationException("Некорректный запрос");
        else {
            dto.setRequester(userId);
            dto.setCreated(LocalDateTime.now());
            itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest((dto)));
        }

        return ItemRequestMapper.toItemRequestDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(int userId) {
        userService.checkUserId(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterOrderByCreatedDesc(userId);

        return prepareItemRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequestById(int userId, long requestId) {
        userService.checkUserId(userId);
        ItemRequestDto itemRequestDto;
        Optional<ItemRequest> itemRequestOptional = requestRepository.findById(requestId);

        if (itemRequestOptional.isPresent()) {
            ItemRequest itemRequest = itemRequestOptional.get();
            List<Item> itemList = itemRepository.findAllByRequestOrderByIdDesc(itemRequest.getId());
            List<ItemDto> itemDtoList = itemList.stream()
                    .map(itemMapper::toItemDto).collect(Collectors.toList());
            itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, itemDtoList);
        } else {
            throw new NotFoundException("Запрос не найден!");
        }

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int userId, int from, int size) {
        userService.checkUserId(userId);
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sortByCreated);
        Iterable<ItemRequest> requests = requestRepository.findAll(page);
        List<ItemRequest> requestList = new ArrayList<>();
        requests.forEach(requestList::add);
        requestList = requestList.stream()
                .filter(request -> request.getRequester() != userId).collect(Collectors.toList());

        return prepareItemRequestDtoList(requestList);
    }

    private List<ItemRequestDto> prepareItemRequestDtoList(List<ItemRequest> requests) {
        List<ItemRequestDto> requestDtoList = new ArrayList<>();

        for (int i = requests.size() - 1; i >= 0; i--) {
            ItemRequest itemRequest = requests.get(i);
            List<Item> itemList = itemRepository.findAllByRequestOrderByIdDesc(itemRequest.getId());
            List<ItemDto> itemDtoList = itemList.stream()
                    .map(itemMapper::toItemDto).collect(Collectors.toList());
            requestDtoList.add(ItemRequestMapper.toItemRequestDto(itemRequest,itemDtoList));
        }

        return requestDtoList;
    }
}
