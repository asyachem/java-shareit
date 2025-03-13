package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items from user {}", userId);
        return itemClient.getItemsFromUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable(value = "id") Long id) {
        log.info("Get item by id {}", id);
        return itemClient.getItemById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchedItems(@RequestParam("text") @NotBlank String text) {
        log.info("Get item by text name and description {}", text);
        return itemClient.getSearchedItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create item {}, user id {}", item, userId);
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable("id") Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto newItemRequest) {
        log.info("Update item id {}, user id {}, new item {}", id, userId, newItemRequest);
        return itemClient.updateItem(id, userId, newItemRequest);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@PathVariable("itemId") Long itemId, @Valid @RequestBody CommentRequest request,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add comment {}, item id {}, user id {}", request, itemId, userId);
        return itemClient.addComment(itemId, userId, request);
    }
}
