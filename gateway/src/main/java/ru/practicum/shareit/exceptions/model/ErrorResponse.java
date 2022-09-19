package ru.practicum.shareit.exceptions.model;

/**
 * Класс для формирования ответа при ошибках
 * Class for generating a response in case of errors
 */
public class ErrorResponse {
    private final String error;                 //Название ошибки

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

