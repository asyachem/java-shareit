package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class RequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto dto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            for (Item item : itemRequest.getItems()) {
                ItemResponse response = new ItemResponse();
                response.setItemId(item.getId());
                response.setName(item.getName());
                response.setOwnerId(item.getOwner().getId());
                itemRequestDto.getItems().add(response);
            }
        }

        return itemRequestDto;
    }
}
