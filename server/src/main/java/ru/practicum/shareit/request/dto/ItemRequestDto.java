package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;

    @NotNull(message = "Отсутствует описание")
    private String description;

    private LocalDateTime created;

    private List<ItemResponse> items = new ArrayList<>();
}
