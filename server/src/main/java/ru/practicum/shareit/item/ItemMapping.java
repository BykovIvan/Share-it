package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
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
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .build();
        if (item.getRequest() == null) {
            return itemDto;
        } else {
            itemDto.setRequestId(item.getRequest().getId());
            return itemDto;
        }
    }

    /**
     * Метод для преобразования Item в ItemDtoWithComments
     * Method to convert Item to ItemDtoWithComments
     */
    public static ItemDtoWithComments toItemDtoWithComments(Long userId, Item item, List<CommentDto> comment, List<BookingDto> bookings) {
        ItemDtoWithComments itemDtoWithComments = ItemDtoWithComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comment)
                .build();

        if (bookings.isEmpty()) {
            return itemDtoWithComments;
        }
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime timeNow = LocalDateTime.now();
            for (BookingDto booking : bookings) {
                if (booking.getStatus().equals(StatusOfItem.APPROVED) ||
                        booking.getStatus().equals(StatusOfItem.WAITING)) {
                    LocalDateTime timeStart = booking.getStart();
                    LocalDateTime timeEnd = booking.getEnd();
                    if (timeStart.isBefore(timeNow) && timeEnd.isBefore(timeNow)) {
                        itemDtoWithComments.setLastBooking(booking);
                    }
                    if (timeStart.isAfter(timeNow) && timeEnd.isAfter(timeNow)) {
                        if (itemDtoWithComments.getNextBooking() == null) {
                            itemDtoWithComments.setNextBooking(booking);
                        }
                    }
                }

            }
        }
        return itemDtoWithComments;
    }

    /**
     * Метод для преобразования Item в ItemDtoForRequest
     * Method to convert Item to ItemDtoForRequest
     */
    public static ItemDtoForRequest toItemDtoForRequest(Item item, Long userId) {
        return ItemDtoForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(userId)
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

    /**
     * Метод для преобразования ItemDto в Item
     * Method to convert ItemDto to Item
     */
    public static Item toItem(ItemDto itemDTO, ItemRequest itemRequest, User user) {
        return Item.builder()
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .request(itemRequest)
                .owner(user)
                .build();

    }
}
