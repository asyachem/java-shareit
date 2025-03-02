package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.util.Date;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private Date created;
}
