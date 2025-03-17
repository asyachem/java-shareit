package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import ru.practicum.shareit.comment.model.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemIncoming;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ItemController.class})
public class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationContext applicationContext;

    private ItemDto testItemDto;
    private List<ItemDto> testItemDtoList;
    private CommentDto testCommentDto;
    private CommentRequest testCommentRequest;

    @BeforeEach
    void setUp() {
        testItemDto = new ItemDto();
        testItemDto.setId(1L);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
        testItemDto.setRequestId(1L);
        testItemDto.setComments(new ArrayList<>());

        testItemDtoList = new ArrayList<>();
        testItemDtoList.add(testItemDto);

        testCommentDto = new CommentDto();
        testCommentDto.setId(1L);
        testCommentDto.setText("Test Comment");
        testCommentDto.setAuthorName("Test User");
        testCommentDto.setCreated(LocalDateTime.now());

        testCommentRequest = new CommentRequest();
        testCommentRequest.setText("Test Comment");
    }

    @Test
    void getItemsFromUser_ShouldReturnListOfItems() throws Exception {
        Long userId = 1L;
        when(itemService.getItemsFromUser(userId)).thenReturn(testItemDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(testItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(testItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(testItemDto.getAvailable())));
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        Long itemId = 1L;
        when(itemService.getItemById(itemId)).thenReturn(testItemDto);

        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testItemDto.getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.getAvailable())));
    }

    @Test
    void getSearchedItems_ShouldReturnListOfItems() throws Exception {
        String searchText = "test";
        when(itemService.getSearchedItems(searchText)).thenReturn(testItemDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(testItemDto.getName())));
    }

    @Test
    void getSearchedItems_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        Long userId = 1L;
        when(itemService.createItem(any(ItemIncoming.class), eq(userId))).thenReturn(testItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testItemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testItemDto.getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.getAvailable())));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        when(itemService.updateItem(eq(itemId), eq(userId), any(ItemIncoming.class))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemService.addComment(eq(itemId), eq(userId), anyString())).thenReturn(testCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCommentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(testCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(testCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }
}
