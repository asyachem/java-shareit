package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;

    @NotNull(message = "Отсутствует email у пользователя")
    private String email;
}
