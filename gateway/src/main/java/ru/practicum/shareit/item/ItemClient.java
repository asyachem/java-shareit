package ru.practicum.shareit.item;

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

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

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

    public ResponseEntity<Object> createItem(ItemDto item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long id, Long userId, ItemDto newItemRequest) {
        return patch("/" + id, userId, newItemRequest);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long userId, CommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }
}
