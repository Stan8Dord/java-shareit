package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService service) {
        this.requestService = service;
    }

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestBody @Valid ItemRequestDto requestDto) {
        return requestService.addNewRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable("requestId") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "32") int size) {
        return requestService.getAllRequests(userId, from, size);
    }
}
