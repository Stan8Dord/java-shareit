package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    private User dummyUser1 = new User(1, "user1", "email1@email.com");
    private Item dummyItem1 = new Item(1, "стол", "дубовый", true, 1, 1);
    private Item dummyItem2 = new Item(2, "name1", "description1", true, 1, 2);

    @Test
    void findByTextOnlyAvailableTest() {
        Pageable page = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"));
        userRepository.save(dummyUser1);
        itemRepository.save(dummyItem1);
        itemRepository.save(dummyItem2);

        Page<ItemDto> itemDtoList = itemRepository.findByTextOnlyAvailable("стол", page);
        Optional<ItemDto> itemDtoOptional = itemDtoList.stream().findFirst();
        Assertions.assertTrue(itemDtoOptional.isPresent());
        ItemDto itemDto = itemDtoOptional.get();
        Assertions.assertEquals("стол", itemDto.getName());
        Assertions.assertEquals("дубовый", itemDto.getDescription());
        Assertions.assertEquals(1, itemDto.getId());
    }
}
