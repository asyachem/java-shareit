package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Необходимо указать название вещи")
    private String name;

    @NotBlank(message = "Необходимо указать описание вещи")
    private String description;

    @NotNull(message = "Необходимо указать статус вещи")
    private Boolean available;

    private Long requestId;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
}
