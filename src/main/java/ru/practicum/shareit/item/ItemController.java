package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsFromUser(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable(value = "id") Long id) {
            return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchedItems(@RequestParam("text") String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        return itemService.getSearchedItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) throws ValidationException {

        if (item.getAvailable() == null) {
            throw new ValidationException("Необходимо указать статус вещи");
        }
        if (item.getName().isBlank()) {
            throw new ValidationException("Необходимо указать название вещи");
        }
        if (item.getDescription().isBlank()) {
            throw new ValidationException("Необходимо указать описание вещи");
        }

        return itemService.createItem(ItemMapper.toItem(item), userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") Long id, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto newItemRequest) {
        return itemService.updateItem(id, userId, ItemMapper.toItem(newItemRequest));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addComment(@PathVariable("itemId") Long itemId, @RequestBody String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, userId, text);
    }
}
