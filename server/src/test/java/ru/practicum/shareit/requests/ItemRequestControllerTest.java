package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;

    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1,
            "desc2",
            1,
            created,
            null);

    @Test
    void addNewRequestTest() throws Exception {
        when(requestService.addNewRequest(anyInt(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                    .content(mapper.writeValueAsString(itemRequestDto))
                    .header("X-Sharer-User-Id", 1)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("desc2")))
                .andExpect(jsonPath("$.requester", is(1)))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void getAllUserRequestsTest() throws Exception {
        when(requestService.getAllUserRequests(anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("desc2")))
                .andExpect(jsonPath("$.[0].requester", is(1)))
                .andExpect(jsonPath("$.[0].created", is(created.toString())));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(anyInt(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("desc2")))
                .andExpect(jsonPath("$.requester", is(1)))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(requestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("desc2")))
                .andExpect(jsonPath("$.[0].requester", is(1)))
                .andExpect(jsonPath("$.[0].created", is(created.toString())));
    }
}
