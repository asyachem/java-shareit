package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long id;

    @NotNull(message = "Дата начала бронирования не может быть пустой")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    private LocalDateTime end;
    private Long itemId;
    private User booker;
    private Status status;
}
