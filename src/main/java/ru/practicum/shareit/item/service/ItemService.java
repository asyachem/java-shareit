package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsFromUser(Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getSearchedItems(String text);

    ItemDto createItem(Item item, Long userId);

    ItemDto updateItem(Long id, Long userId, Item newItemRequest);

    ItemDto addComment(Long itemId, Long userId, String comment);
}
