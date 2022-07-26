package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Класс для преобразования объекса Item в объект ItemDto для предоставления пользователю и обратно
 * Class for converting an Item object to an ItemDto object for presentation to the user and vice versa
 */
public class ItemMapper {
    /**
     * Метод для преобразования Item в ItemDto
     * Method to convert Item to ItemDto
     */
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();

    }

    /**
     * Метод для преобразования ItemDto в Item
     * Method to convert ItemDto to Item
     */
    public static Item toItem(ItemDto itemDTO, User user) {
        return Item.builder()
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .owner(user)
                .build();

    }

}
