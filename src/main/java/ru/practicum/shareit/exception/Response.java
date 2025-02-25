package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {

    private String message;

    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

}
