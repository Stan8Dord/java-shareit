package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto dummyDto1 = new UserDto(1, "user1", "email1@email.com");
    private UserDto dummyDto2 = new UserDto(1, "userNew", "emailNew@email.com");

    @Test
    void creatUserTest() throws Exception {
        when(userService.createUser(any())).thenReturn(dummyDto1);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(dummyDto1))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dummyDto1.getId()),Integer.class))
                .andExpect(jsonPath("$.name", is(dummyDto1.getName())))
                .andExpect(jsonPath("$.email", is(dummyDto1.getEmail())));
    }

    @Test
    void updateUserTest() throws  Exception {
        when(userService.updateUser(anyInt(), any())).thenReturn(dummyDto2);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dummyDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dummyDto2.getId()),Integer.class))
                .andExpect(jsonPath("$.name", is(dummyDto2.getName())))
                .andExpect(jsonPath("$.email", is(dummyDto2.getEmail())));
    }

    @Test
    void getUserByIdTest() throws  Exception {
        when(userService.getUserById(anyInt())).thenReturn(dummyDto2);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dummyDto2.getId()),Integer.class))
                .andExpect(jsonPath("$.name", is(dummyDto2.getName())))
                .andExpect(jsonPath("$.email", is(dummyDto2.getEmail())));
    }

    @Test
    void getAllUsersTest() throws  Exception {
        when(userService.getAllUsers()).thenReturn(List.of(dummyDto1, dummyDto2));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[1].id", is(dummyDto2.getId()),Integer.class))
                .andExpect(jsonPath("$.[1].name", is(dummyDto2.getName())))
                .andExpect(jsonPath("$.[0].email", is(dummyDto1.getEmail())));
    }

    @Test
    void deleteUserTest() throws  Exception {
        Mockito.doNothing().when(userService).deleteUser(anyInt());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(Mockito.anyInt());
    }
}
