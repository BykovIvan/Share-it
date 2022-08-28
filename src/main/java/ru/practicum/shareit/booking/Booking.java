package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp start;
    private Timestamp end;
    @ManyToOne
    private Item item;                      //Вещь
    @ManyToOne
    private User booker;                    //Пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    private StatusOfItem status;            //статус вещи, должен выставлять пользователь
    private String review;                  //Отзыв, оставляется после успешного бронирования
}
