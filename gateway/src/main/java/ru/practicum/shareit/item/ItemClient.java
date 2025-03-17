package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemIncoming;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItemsFromUser(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getItemById(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getSearchedItems(String text) {
        return get(API_PREFIX + "/search/" + text);
    }

    public ResponseEntity<ItemDto> createItem(ItemIncoming item, Long userId) {
        ResponseEntity<Object> response = post("", userId, item);

        try {
            ItemDto itemDto = objectMapper.convertValue(response.getBody(), ItemDto.class);
            return new ResponseEntity<>(itemDto, response.getHeaders(), response.getStatusCode());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Не удалось преобразовать тело ответа в ItemDto: " + e.getMessage());
        }
    }

    public ResponseEntity<ItemDto> updateItem(Long id, Long userId, ItemIncoming newItemRequest) {
        ResponseEntity<Object> response = patch("/" + id, userId, newItemRequest);

        try {
            ItemDto itemDto = objectMapper.convertValue(response.getBody(), ItemDto.class);
            return new ResponseEntity<>(itemDto, response.getHeaders(), response.getStatusCode());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Ожидался объект типа ItemDto, но получен: " + response.getBody().getClass());
        }
    }

    public ResponseEntity<Object> addComment(Long itemId, Long userId, CommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }
}
