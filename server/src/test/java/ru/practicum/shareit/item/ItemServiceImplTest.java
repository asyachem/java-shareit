package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemServiceImpl itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
    }

    /*getItemsFromUser*/
    @Test
    void shouldReturnItemsByUserIdTest() {
        List<ItemDto> items = itemService.getItemsFromUser(user.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    /*getItemById*/
    @Test
    void shouldGetItemByIdTest() {
        ItemDto foundItem = itemService.getItemById(item.getId());

        assertNotNull(foundItem);
        assertEquals(item.getName(), foundItem.getName());
    }

    /*getSearchedItems*/
    @Test
    void shouldSearchItemsByText() {
        List<ItemDto> items = itemService.getSearchedItems("Test");

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    /*createItem*/
    @Test
    void shouldCreateNewItem() {
        ItemDto newItem = new ItemDto();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);

        ItemDto createdItem = itemService.createItem(newItem, user.getId());

        assertNotNull(createdItem);
        assertEquals(newItem.getName(), createdItem.getName());
    }

    /*updateItem*/
    @Test
    void shouldUpdateItem() {
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");

        ItemDto updatedItem = itemService.updateItem(item.getId(), user.getId(), updatedItemDto);

        assertNotNull(updatedItem);
        assertEquals(updatedItemDto.getName(), updatedItem.getName());
    }

    /*addComment*/
    @Test
    void shouldAddComment() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = itemService.addComment(item.getId(), user.getId(), "Все супер");

        assertNotNull(commentDto);
        assertEquals("Все супер", commentDto.getText());
    }
}
