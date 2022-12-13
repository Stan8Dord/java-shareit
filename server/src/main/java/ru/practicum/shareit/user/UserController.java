package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST: \n" + userDto);

        return service.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") int userId, @Valid @RequestBody UserDto userDto) {
        log.info("PUT: \n" + userDto);

        return service.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") int id) {
        log.info("GET: \n" + id);

        return service.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        System.out.println("No, it's not work(");
        return service.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") int id) {
        log.info("DELETE: userId\n" + id);

        service.deleteUser(id);
    }
}
