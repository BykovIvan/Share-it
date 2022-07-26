package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    private Long bookingId;
    private LocalDate start;
    private LocalDate end;
    private Item item;                      //Вещь
    private User booker;                    //Пользователь, который осуществляет бронирование
    private StatusOfItem status;            //статус вещи, должен выставлять пользователь
    private String review;                  //Отзыв, оставляется после успешного бронирования
}
