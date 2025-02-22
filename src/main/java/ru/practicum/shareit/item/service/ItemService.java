package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems();

    ItemDto getItemById(long id);

    List<ItemDto> getSearchedItems(String text);

    ItemDto createItem(Item item, long userId);

    ItemDto updateItem(long id, long userId, Item newItemRequest);
}
