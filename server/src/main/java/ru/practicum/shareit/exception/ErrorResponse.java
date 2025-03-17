package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private int status;

    public ErrorResponse(String message, int value) {
        this.error = message;
        this.status = value;
    }
}
