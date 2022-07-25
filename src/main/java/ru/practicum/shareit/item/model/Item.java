package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {
    private Long itemId;
    private String name;                         //Имя
    private String description;                  //Описание
    private Boolean available;                      //количество, сколько раз вещь была в аренде
    private User owner;                          //Владелец
    private ItemRequest request;                 //ссылка на запрос вещи
}
