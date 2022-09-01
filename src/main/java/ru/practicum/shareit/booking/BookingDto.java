package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.UserDto;

import java.sql.Timestamp;

@Data
@Builder
public class BookingDto {
    private Long id;
    private Timestamp start;
    private Timestamp end;
    private ItemDto item;                      //Вещь
    private Long itemId;
    private Long owner;
    private UserDto booker;                    //Пользователь, который осуществляет бронирование
    private StatusOfItem status;            //статус вещи, должен выставлять пользователь
//    private String review;                  //Отзыв, оставляется после успешного бронирования
}
