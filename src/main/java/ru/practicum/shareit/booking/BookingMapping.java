package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapping;

/**
 * Класс для преобразования объекса Booking в объект BookingDto для предоставления пользователю и обратно
 * Class for converting an Booking object to an BookingDto object for presentation to the user and vice versa
 */
public class BookingMapping {
    /**
     * Метод для преобразования Booking в BookingDto
     * Method to convert Booking to BookingDto
     */
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapping.toItemDto(booking.getItem()))
                .itemId(booking.getItem().getId())
                .status(booking.getStatus())
                .booker(UserMapping.toUserDto(booking.getBooker()))
                .owner(booking.getItem().getOwner().getId())
                .build();
    }

    /**
     * Метод для преобразования BookingDto в Booking
     * Method to convert BookingDto to Booking
     */
    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        return Booking.builder()
                .booker(booker)
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(StatusOfItem.WAITING)
                .build();
    }
}
