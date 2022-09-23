package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private Timestamp start;
    @Column(name = "end_date")
    private Timestamp end;
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;                      //Вещь
    @ManyToOne(fetch = FetchType.EAGER)
    private User booker;                    //Пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    private StatusOfItem status;            //статус вещи, должен выставлять пользователь
}
