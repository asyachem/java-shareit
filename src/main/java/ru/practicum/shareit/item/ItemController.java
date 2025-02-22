package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems() {
        return itemService.getItems();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchedItems(@RequestParam("text") String text) {
        return itemService.getSearchedItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") Long id, @RequestHeader("X-Sharer-User-Id") long userId, @RequestBody Item newItemRequest) {
        return itemService.updateItem(id, userId, newItemRequest);
    }


}
