package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.User;

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
                .bookingId(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .nameItem(booking.getItem().getName())
                .status(booking.getStatus())
                .booker(booking.getBooker())
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
