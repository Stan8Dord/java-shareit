package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    ItemDto itemDto = new ItemDto(
            1L,
            "name",
            "description",
            true,
            12);
    CommentDto commentDto = new CommentDto(
            1,
            "comment 1",
            "Batman",
            created);
    ItemOwnerDto itemOwnerDto = new ItemOwnerDto(
            1L,
            "name",
            "description",
            true,
            null,
            null,
            List.of(commentDto));

    @Test
    void addNewItemTest() throws Exception {
        when(itemService.addNewItem(anyInt(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                    .content(mapper.writeValueAsString(itemDto))
                    .header("X-Sharer-User-Id", 1)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(12)));
    }

    @Test
    void modifyItemTest() throws Exception {
        when(itemService.modifyItem(anyLong(), any(), anyInt())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(12)));
    }

    @Test
    void addNewCommentTest() throws Exception {
        when(itemService.addNewComment(anyInt(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("comment 1")))
                .andExpect(jsonPath("$.authorName", is("Batman")))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemOwnerDtoById(anyInt(), anyLong())).thenReturn(itemOwnerDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("comment 1")))
                .andExpect(jsonPath("$.comments[0].authorName", is("Batman")))
                .andExpect(jsonPath("$.comments[0].created", is(created.toString())));
    }

    @Test
    void getItemsTest() throws Exception {
        when(itemService.getItems(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemOwnerDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("name")))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].available", is(true)))
                .andExpect(jsonPath("$.[0].comments[0].id", is(1)))
                .andExpect(jsonPath("$.[0].comments[0].text", is("comment 1")))
                .andExpect(jsonPath("$.[0].comments[0].authorName", is("Batman")))
                .andExpect(jsonPath("$.[0].comments[0].created", is(created.toString())));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=test")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("name")))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].available", is(true)))
                .andExpect(jsonPath("$.[0].requestId", is(12)));
    }
}
