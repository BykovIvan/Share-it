package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * Класс для преобразования объекса Item в объект ItemDto для предоставления пользователю и обратно
 * Class for converting an Item object to an ItemDto object for presentation to the user and vice versa
 */

public class ItemMapping {
    /**
     * Метод для преобразования Item в ItemDto
     * Method to convert Item to ItemDto
     */
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
//                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();

    }

    /**
     * Метод для преобразования Item в ItemDtoWithComments
     * Method to convert Item to ItemDtoWithComments
     */
    public static ItemDtoWithComments toItemDtoWithComments(Item item, List<Comment> comment) {
        return ItemDtoWithComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comment(comment)
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
