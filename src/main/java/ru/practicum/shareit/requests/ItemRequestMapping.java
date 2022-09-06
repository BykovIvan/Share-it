package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.user.User;

/**
 * Класс для преобразования объекса ItemRequest в объект ItemRequestDto для предоставления пользователю и обратно
 * Class for converting an Booking object to an BookingDto object for presentation to the user and vice versa
 */
public class ItemRequestMapping {
    /**
     * Метод для преобразования ItemRequest в ItemRequestDto
     * Method to convert ItemRequest to ItemRequestDto
     */
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, ItemDtoForRequest item) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .item(item)
                .build();

    }

    /**
     * Метод для преобразования ItemRequestDto в ItemRequest
     * Method to convert ItemRequestDto to ItemRequest
     */
    public static ItemRequest toItemRequest(ItemRequestDto ItemRequestDto, User user) {
        return ItemRequest.builder()
                .id(ItemRequestDto.getId())
                .requestor(user)
                .description(ItemRequestDto.getDescription())
                .build();

    }
}
