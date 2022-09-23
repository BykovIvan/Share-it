package ru.practicum.shareit.exceptions;

public class NoUserInHeaderException extends RuntimeException {
    public NoUserInHeaderException(String message) {
        super(message);
    }
}
