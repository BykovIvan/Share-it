package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

public class Booking {
    private Long bookingId;
    private LocalDate start;
    private LocalDate end;
    private Item item;                      //Вещь
    private User booker;                    //Пользователь, который осуществляет бронирование
    private StatusOfItem status;
    private String review;                  //Отзыв
}
