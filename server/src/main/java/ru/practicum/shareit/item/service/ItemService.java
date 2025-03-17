package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemIncoming;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsFromUser(Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> getSearchedItems(String text);

    ItemDto createItem(ItemIncoming item, Long userId);

    ItemDto updateItem(Long id, Long userId, ItemIncoming newItemRequest);

    CommentDto addComment(Long itemId, Long userId, String text);
}
