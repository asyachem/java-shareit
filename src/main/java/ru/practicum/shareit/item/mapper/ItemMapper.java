package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static Item updateItemField(Item item, Item newItemRequest) {
        if (newItemRequest.getName() != null) {
            item.setName(newItemRequest.getName());
        }

        if (newItemRequest.getDescription() != null) {
            item.setDescription(newItemRequest.getDescription());
        }

        if (newItemRequest.getAvailable() != null) {
            item.setAvailable(newItemRequest.getAvailable());
        }

        return item;
    }
}
