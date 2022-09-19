package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.model.ErrorResponse;

@RestControllerAdvice
public class ExceptionHandler {

    /**
     * Все ситуаций, когда искомый объект не найден, код 404
     * All situations when the desired object is not found
     */
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameterException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Ошибки сервера, код 500
     * Error of server
     */
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIncorrectParameterException(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Ошибки сервера, код 500
     * Error of server
     */
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIncorrectParameterException(final MissingServletRequestParameterException e) {
        return new ErrorResponse("В параметрах не указан статус!");
    }

    /**
     * Плохой запрос, код 400
     * Bad Request
     */
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final BadRequestException e) {
        return new ErrorResponse("Плохо составленный запрос! Проверь данные!");
    }

    /**
     * Ошибка в запросе, код 400
     * Error in request
     */
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final Throwable e) {
        return new ErrorResponse("Плохо составленный запрос! Проверь данные! Или сервер упал! Упс!");
    }
}
