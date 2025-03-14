package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
}
