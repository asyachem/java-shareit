package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text; // содержимое комментария
    private ItemDto item; // вещь, к которой относится комментарий
    private String authorName; // автор комментария
    private LocalDateTime created; // дата создания комментария
}
