package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;                      //Вещь
    private Long itemId;
    private Long owner;
    private UserDto booker;                    //Пользователь, который осуществляет бронирование
    private Long bookerId;
    private StatusOfItem status;            //статус вещи, должен выставлять пользователь
}
