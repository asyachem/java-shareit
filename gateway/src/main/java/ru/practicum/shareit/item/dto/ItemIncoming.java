package ru.practicum.shareit.item.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemIncoming {
    private Long id;

    @NotBlank(message = "Необходимо указать название вещи")
    private String name;

    @NotBlank(message = "Необходимо указать описание вещи")
    private String description;

    @NotNull(message = "Необходимо указать статус вещи")
    private Boolean available;

    private Long requestId;
}
