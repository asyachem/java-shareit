package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long itemId;
    private String name;
    private Long ownerId;
}
