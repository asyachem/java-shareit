package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private BookingDto testBookingDto;
    private List<BookingDto> testBookingDtoList;
    private BookingRequest testBookingRequest;
    private ItemDto testItemDto;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Item Description");
        testItemDto.setAvailable(true);

        testUserDto = new UserDto();
        testUserDto.setId(2L);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@example.com");

        testBookingDto = new BookingDto();
        testBookingDto.setId(1L);
        testBookingDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        testBookingDto.setItem(testItemDto);
        testBookingDto.setBooker(testUserDto);
        testBookingDto.setStatus(Status.WAITING);

        testBookingDtoList = new ArrayList<>();
        testBookingDtoList.add(testBookingDto);

        BookingDto secondBooking = new BookingDto();
        secondBooking.setId(2L);
        secondBooking.setStart(LocalDateTime.now().plusDays(3));
        secondBooking.setEnd(LocalDateTime.now().plusDays(4));
        secondBooking.setItem(testItemDto);
        secondBooking.setBooker(testUserDto);
        secondBooking.setStatus(Status.APPROVED);
        testBookingDtoList.add(secondBooking);

        testBookingRequest = new BookingRequest();
        testBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        testBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
        testBookingRequest.setItemId(1L);
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 2L;
        when(bookingService.getBooking(bookingId, userId)).thenReturn(testBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(testItemDto.getName())))
                .andExpect(jsonPath("$.booker.id", is(testUserDto.getId().intValue())))
                .andExpect(jsonPath("$.status", is(testBookingDto.getStatus().toString())));
    }

    @Test
    void getAllUserBookingByState_ShouldReturnBookingsList() throws Exception {
        Long userId = 2L;
        when(bookingService.getAllUserBookingByState(eq(userId), any(StateBooking.class)))
                .thenReturn(testBookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testBookingDto.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(testBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("APPROVED")));
    }

    @Test
    void getAllUserBooking_ShouldReturnBookingsList() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllUserBookingByState(eq(userId), any(StateBooking.class)))
                .thenReturn(testBookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testBookingDto.getId().intValue())))
                .andExpect(jsonPath("$[0].item.id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        Long userId = 2L;
        when(bookingService.createBooking(any(BookingRequest.class), eq(userId)))
                .thenReturn(testBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(testUserDto.getId().intValue())))
                .andExpect(jsonPath("$.status", is(testBookingDto.getStatus().toString())));
    }

    @Test
    void approvedBooking_ShouldReturnUpdatedBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        BookingDto approvedBookingDto = new BookingDto();
        approvedBookingDto.setId(bookingId);
        approvedBookingDto.setStart(testBookingDto.getStart());
        approvedBookingDto.setEnd(testBookingDto.getEnd());
        approvedBookingDto.setItem(testItemDto);
        approvedBookingDto.setBooker(testUserDto);
        approvedBookingDto.setStatus(Status.APPROVED);

        when(bookingService.approvedBooking(eq(bookingId), eq(userId), eq(approved)))
                .thenReturn(approvedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingId.intValue())))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getAllUserBookingByState_WithoutState_ShouldUseDefaultState() throws Exception {
        Long userId = 2L;
        when(bookingService.getAllUserBookingByState(eq(userId), eq(StateBooking.ALL)))
                .thenReturn(testBookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllUserBooking_WithoutState_ShouldUseDefaultState() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllUserBookingByState(eq(userId), eq(StateBooking.ALL)))
                .thenReturn(testBookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
