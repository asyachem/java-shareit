package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsFromUser(Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getSearchedItems(String text);

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto updateItem(Long id, Long userId, ItemDto newItemRequest);

    CommentDto addComment(Long itemId, Long userId, String text);
}
