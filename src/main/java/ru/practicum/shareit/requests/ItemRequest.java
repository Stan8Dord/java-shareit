package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private Instant created;
}
