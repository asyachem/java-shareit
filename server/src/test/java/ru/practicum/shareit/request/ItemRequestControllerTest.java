package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationContext applicationContext;

    private ItemRequestDto testItemRequestDto;
    private List<ItemRequestDto> testItemRequestDtoList;
    private ItemResponse testItemResponse;

    @BeforeEach
    void setUp() {
        testItemResponse = new ItemResponse();
        testItemResponse.setItemId(1L);
        testItemResponse.setName("Test Item");
        testItemResponse.setOwnerId(5L);

        testItemRequestDto = new ItemRequestDto();
        testItemRequestDto.setId(1L);
        testItemRequestDto.setDescription("Test Request Description");
        testItemRequestDto.setCreated(LocalDateTime.now());
        testItemRequestDto.setItems(Arrays.asList(testItemResponse));

        testItemRequestDtoList = new ArrayList<>();
        testItemRequestDtoList.add(testItemRequestDto);

        ItemRequestDto secondRequest = new ItemRequestDto();
        secondRequest.setId(2L);
        secondRequest.setDescription("Second Test Request");
        secondRequest.setCreated(LocalDateTime.now().minusDays(1));
        secondRequest.setItems(new ArrayList<>());
        testItemRequestDtoList.add(secondRequest);
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        Long userId = 1L;

        ItemRequestDto creationDto = new ItemRequestDto();
        creationDto.setId(1L);
        creationDto.setDescription("Test Request Description");
        creationDto.setCreated(LocalDateTime.now());
        creationDto.setItems(Arrays.asList(testItemResponse));

        when(requestService.createRequest(creationDto, userId)).thenReturn(testItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemId", is(testItemResponse.getItemId().intValue())))
                .andExpect(jsonPath("$.items[0].name", is(testItemResponse.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(testItemResponse.getOwnerId().intValue())));
    }

    @Test
    void getUserRequests_ShouldReturnRequestsList() throws Exception {
        Long userId = 1L;
        when(requestService.getUserRequests(userId)).thenReturn(testItemRequestDtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testItemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].itemId", is(testItemResponse.getItemId().intValue())))
                .andExpect(jsonPath("$[0].items[0].name", is(testItemResponse.getName())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(testItemResponse.getOwnerId().intValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Second Test Request")));
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() throws Exception {
        when(requestService.getAllRequests()).thenReturn(testItemRequestDtoList);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testItemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Second Test Request")));
    }

    @Test
    void getInfoRequest_ShouldReturnRequestInfo() throws Exception {
        Long requestId = 1L;
        when(requestService.getInfoRequest(requestId)).thenReturn(testItemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].itemId", is(testItemResponse.getItemId().intValue())))
                .andExpect(jsonPath("$.items[0].name", is(testItemResponse.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(testItemResponse.getOwnerId().intValue())));
    }

    @Test
    void getUserRequests_WithEmptyResult_ShouldReturnEmptyList() throws Exception {
        Long userId = 2L;
        when(requestService.getUserRequests(userId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getInfoRequest_WithNoItems_ShouldReturnEmptyItemsList() throws Exception {
        Long requestId = 2L;

        ItemRequestDto emptyItemsRequest = new ItemRequestDto();
        emptyItemsRequest.setId(requestId);
        emptyItemsRequest.setDescription("Request with no items");
        emptyItemsRequest.setCreated(LocalDateTime.now());
        emptyItemsRequest.setItems(new ArrayList<>());

        when(requestService.getInfoRequest(requestId)).thenReturn(emptyItemsRequest);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.description", is("Request with no items")))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}
