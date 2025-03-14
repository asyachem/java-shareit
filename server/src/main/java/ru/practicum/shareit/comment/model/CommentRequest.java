package ru.practicum.shareit.comment.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
