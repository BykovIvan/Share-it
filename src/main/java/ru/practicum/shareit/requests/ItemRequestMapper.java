package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestDto;

/**
 * Класс для преобразования объекса ItemRequest в объект ItemRequestDto для предоставления пользователю и обратно
 * Class for converting an Booking object to an BookingDto object for presentation to the user and vice versa
 */
public class ItemRequestMapper {
    /**
     * Метод для преобразования ItemRequest в ItemRequestDto
     * Method to convert ItemRequest to ItemRequestDto
     */
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();

    }

    /**
     * Метод для преобразования ItemRequestDto в ItemRequest
     * Method to convert ItemRequestDto to ItemRequest
     */
    public static ItemRequest toItemRequest(ItemRequestDto ItemRequestDto) {
        return ItemRequest.builder()
                .id(ItemRequestDto.getId())
                .description(ItemRequestDto.getDescription())
                .build();

    }
}
