package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream().map(CommentMapper::toCommentDto).toList());
        }

        return itemDto;
    }

    public static Item updateItemField(Item item, ItemDto newItemRequest) {
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

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(itemRequest);

        return item;
    }
}
