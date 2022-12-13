package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private ItemDto itemDto = new ItemDto(3, "name", "description", true, 12);
    private List<ItemDto> itemDtoList = new ArrayList<>(List.of(itemDto));

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "super request",
                7,
                created,
                itemDtoList);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("super request");
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(12);
    }
}
