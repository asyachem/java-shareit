package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemIncoming;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest()
@Import(ItemServiceImpl.class)
public class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private ItemRepository itemRepositoryMock;

    @MockBean
    private BookingRepository bookingRepositoryMock;

    @MockBean
    private CommentRepository commentRepositoryMock;

    @MockBean
    private RequestRepository requestRepositoryMock;

    private User testUser;
    private Item testItem;
    private ItemIncoming testItemIncoming;
    private ItemDto testItemDto;
    private Comment testComment;
    private CommentDto testCommentDto;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        testItem.setOwner(testUser);

        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
        testItem.setAvailable(true);
        testItem.setOwner(testUser);

        testItemIncoming = new ItemIncoming();
        testItemDto.setId(1L);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Test Comment");
        testComment.setAuthor(testUser);
        testComment.setItem(testItem);
        testComment.setCreated(LocalDateTime.now());

        testCommentDto = new CommentDto();
        testCommentDto.setId(1L);
        testCommentDto.setText("Test Comment");
        testCommentDto.setAuthorName(testUser.getName());
        testCommentDto.setCreated(LocalDateTime.now());

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setBooker(testUser);
        testBooking.setItem(testItem);
        testBooking.setStatus(Status.APPROVED);
        testBooking.setStart(LocalDateTime.now().minusDays(1));
        testBooking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void getItemsFromUser_ShouldReturnListOfItems() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.findByOwnerIdWithComments(any(Long.class))).thenReturn(Collections.singletonList(testItem));

        List<ItemDto> result = itemService.getItemsFromUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItemDto.getName(), result.get(0).getName());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).findByOwnerIdWithComments(1L);
    }

    @Test
    void getItemsFromUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsFromUser(1L));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, never()).findByOwnerIdWithComments(any(Long.class));
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.of(testItem));

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(testItemDto.getName(), result.getName());
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L));
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
    }

    @Test
    void getSearchedItems_ShouldReturnListOfItems() {
        when(itemRepositoryMock.findByNameOrDescriptionWithComments(any(String.class))).thenReturn(Collections.singletonList(testItem));

        List<ItemDto> result = itemService.getSearchedItems("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItemDto.getName(), result.get(0).getName());
        verify(itemRepositoryMock, times(1)).findByNameOrDescriptionWithComments("Test");
    }

    @Test
    void createItem_ShouldReturnCreatedItem() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.createItem(testItemIncoming, 1L);

        assertNotNull(result);
        assertEquals(testItemDto.getName(), result.getName());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(testItemIncoming, 1L));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.of(testItem));
        when(itemRepositoryMock.save(any(Item.class))).thenReturn(testItem);

        ItemDto result = itemService.updateItem(1L, 1L, testItemIncoming);

        assertNotNull(result);
        assertEquals(testItemDto.getName(), result.getName());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
        verify(itemRepositoryMock, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, testItemIncoming));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, never()).findByIdWithComments(any(Long.class));
        verify(itemRepositoryMock, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowValidationException_WhenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(anotherUser));
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.of(testItem));

        assertThrows(ValidationException.class, () -> itemService.updateItem(1L, 2L, testItemIncoming));
        verify(userRepositoryMock, times(1)).findById(2L);
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
        verify(itemRepositoryMock, never()).save(any(Item.class));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() {
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.of(testItem));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(bookingRepositoryMock.findAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(Collections.singletonList(testBooking));
        when(commentRepositoryMock.save(any(Comment.class))).thenReturn(testComment);

        testBooking.setStart(LocalDateTime.now().minusDays(2));
        testBooking.setEnd(LocalDateTime.now().minusDays(1));

        CommentDto result = itemService.addComment(1L, 1L, "Test Comment");

        assertNotNull(result);
        assertEquals(testCommentDto.getText(), result.getText());
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, times(1)).findAllByBookerIdOrderByStartDesc(1L);
        verify(commentRepositoryMock, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, "Test Comment"));
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
        verify(userRepositoryMock, never()).findById(any(Long.class));
        verify(bookingRepositoryMock, never()).findAllByBookerIdOrderByStartDesc(any(Long.class));
        verify(commentRepositoryMock, never()).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowValidationException_WhenUserIsNotBooker() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        when(itemRepositoryMock.findByIdWithComments(any(Long.class))).thenReturn(Optional.of(testItem));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(anotherUser));
        when(bookingRepositoryMock.findAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 2L, "Test Comment"));
        verify(itemRepositoryMock, times(1)).findByIdWithComments(1L);
        verify(userRepositoryMock, times(1)).findById(2L);
        verify(bookingRepositoryMock, times(1)).findAllByBookerIdOrderByStartDesc(2L);
        verify(commentRepositoryMock, never()).save(any(Comment.class));
    }
}
