package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(BookingServiceImpl.class)
public class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private ItemRepository itemRepositoryMock;

    @MockBean
    private BookingRepository bookingRepositoryMock;

    private User testUser;
    private User testOwner;
    private Item testItem;
    private Booking testBooking;
    private BookingRequest testBookingRequest;
    private BookingDto testBookingDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testOwner = new User();
        testOwner.setId(2L);
        testOwner.setName("Test Owner");
        testOwner.setEmail("owner@example.com");

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        testItem.setOwner(testOwner);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStart(LocalDateTime.now().plusDays(1));
        testBooking.setEnd(LocalDateTime.now().plusDays(2));
        testBooking.setItem(testItem);
        testBooking.setBooker(testUser);
        testBooking.setStatus(Status.WAITING);

        testBookingRequest = new BookingRequest();
        testBookingRequest.setId(1L);
        testBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        testBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        testBookingRequest.setItemId(1L);

        testBookingDto = new BookingDto();
        testBookingDto.setId(1L);
        testBookingDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        testBookingDto.setItem(new ItemDto());
        testBookingDto.setBooker(new UserDto());
        testBookingDto.setStatus(Status.WAITING);
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testItem));
        when(bookingRepositoryMock.findAllByStatusAndItemId(any(Status.class), any(Long.class))).thenReturn(Collections.emptyList());
        when(bookingRepositoryMock.save(any(Booking.class))).thenReturn(testBooking);

        BookingDto result = bookingService.createBooking(testBookingRequest, 1L);

        assertNotNull(result);
        assertEquals(testBookingDto.getStart(), result.getStart());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, times(1)).findAllByStatusAndItemId(Status.APPROVED, 1L);
        verify(bookingRepositoryMock, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testBookingRequest, 1L));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, never()).findById(any(Long.class));
        verify(bookingRepositoryMock, never()).findAllByStatusAndItemId(any(Status.class), any(Long.class));
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testBookingRequest, 1L));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, never()).findAllByStatusAndItemId(any(Status.class), any(Long.class));
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowValidationException_WhenTimeOverlap() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(itemRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testItem));
        when(bookingRepositoryMock.findAllByStatusAndItemId(any(Status.class), any(Long.class))).thenReturn(Collections.singletonList(testBooking));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(testBookingRequest, 1L));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(itemRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, times(1)).findAllByStatusAndItemId(Status.APPROVED, 1L);
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void approvedBooking_ShouldReturnApprovedBooking() {
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testBooking));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testOwner));
        when(bookingRepositoryMock.save(any(Booking.class))).thenReturn(testBooking);

        BookingDto result = bookingService.approvedBooking(1L, 2L, true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findById(2L);
        verify(bookingRepositoryMock, times(1)).save(any(Booking.class));
    }

    @Test
    void approvedBooking_ShouldThrowNotFoundException_WhenBookingNotFound() {
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, 2L, true));
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findById(any(Long.class));
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void approvedBooking_ShouldThrowValidationException_WhenUserIsNotOwner() {
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testBooking));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));

        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1L, 1L, true));
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void approvedBooking_ShouldThrowValidationException_WhenBookingNotWaiting() {
        testBooking.setStatus(Status.APPROVED);
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testBooking));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testOwner));

        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1L, 2L, true));
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findById(2L);
        verify(bookingRepositoryMock, never()).save(any(Booking.class));
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testBooking));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertNotNull(result);
        assertEquals(testBookingDto.getStart(), result.getStart());
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test
    void getBooking_ShouldThrowNotFoundException_WhenBookingNotFound() {
        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findById(any(Long.class));
    }

    @Test
    void getBooking_ShouldThrowValidationException_WhenUserIsNotBookerOrOwner() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        when(bookingRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testBooking));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(anotherUser));

        assertThrows(ValidationException.class, () -> bookingService.getBooking(1L, 3L));
        verify(bookingRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).findById(3L);
    }

    @Test
    void getAllUserBookingByState_ShouldReturnListOfBookings() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(bookingRepositoryMock.findAllByBookerIdOrderByStartDesc(any(Long.class))).thenReturn(Collections.singletonList(testBooking));

        List<BookingDto> result = bookingService.getAllUserBookingByState(1L, StateBooking.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookingDto.getStart(), result.get(0).getStart());
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, times(1)).findAllByBookerIdOrderByStartDesc(1L);
    }

    @Test
    void getAllUserBookingByState_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllUserBookingByState(1L, StateBooking.ALL));
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(bookingRepositoryMock, never()).findAllByBookerIdOrderByStartDesc(any(Long.class));
    }
}
