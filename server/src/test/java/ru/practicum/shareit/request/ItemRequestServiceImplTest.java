package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(RequestServiceImpl.class)
public class ItemRequestServiceImplTest {
    @Autowired
    private RequestServiceImpl requestService;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private ItemRequest testItemRequest;
    private ItemRequestDto testItemRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testItemRequest = new ItemRequest();
        testItemRequest.setId(1L);
        testItemRequest.setDescription("Test Description");
        testItemRequest.setRequester(testUser);
        testItemRequest.setCreated(LocalDateTime.now());

        testItemRequestDto = new ItemRequestDto();
        testItemRequestDto.setId(1L);
        testItemRequestDto.setDescription("Test Description");
        testItemRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(testItemRequest);

        ItemRequestDto result = requestService.createRequest(testItemRequestDto, 1L);

        assertNotNull(result);
        assertEquals(testItemRequestDto.getDescription(), result.getDescription());
        verify(userRepository, times(1)).findById(1L);
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(testItemRequestDto, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_ShouldReturnListOfRequests() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(any(Long.class))).thenReturn(Collections.singletonList(testItemRequest));

        List<ItemRequestDto> result = requestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItemRequestDto.getDescription(), result.get(0).getDescription());
        verify(userRepository, times(1)).findById(1L);
        verify(requestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void getUserRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(requestRepository, never()).findAllByRequesterIdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void getAllRequests_ShouldReturnListOfRequests() {
        when(requestRepository.findAllByOrderByCreatedDesc()).thenReturn(Collections.singletonList(testItemRequest));

        List<ItemRequestDto> result = requestService.getAllRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItemRequestDto.getDescription(), result.get(0).getDescription());
        verify(requestRepository, times(1)).findAllByOrderByCreatedDesc();
    }

    @Test
    void getInfoRequest_ShouldReturnRequest() {
        when(requestRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(testItemRequest));

        ItemRequestDto result = requestService.getInfoRequest(1L);

        assertNotNull(result);
        assertEquals(testItemRequestDto.getDescription(), result.getDescription());
        verify(requestRepository, times(1)).findById(1L);
    }
}
