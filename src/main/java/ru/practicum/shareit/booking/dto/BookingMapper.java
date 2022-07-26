package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Класс для преобразования объекса Booking в объект BookingDto для предоставления пользователю и обратно
 * Class for converting an Booking object to an BookingDto object for presentation to the user and vice versa
 */
public class BookingMapper {
    /**
     * Метод для преобразования Booking в BookingDto
     * Method to convert Booking to BookingDto
     */
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();

    }

    /**
     * Метод для преобразования BookingDto в Booking
     * Method to convert BookingDto to Booking
     */
    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return Booking.builder()
                .bookingId(bookingDto.getBookingId())
                .booker(user)
                .item(item)
                .review(bookingDto.getReview())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

    }
}
