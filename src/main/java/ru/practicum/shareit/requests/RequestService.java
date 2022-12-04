package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addNewRequest(int userId, ItemRequestDto dto);

    List<ItemRequestDto> getAllUserRequests(int userId);

    ItemRequestDto getRequestById(int userId, long requestId);

    List<ItemRequestDto> getAllRequests(int userId, int from, int size);
}
