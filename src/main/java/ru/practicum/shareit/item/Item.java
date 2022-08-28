package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long itemId;
    private String name;                         //Имя вещи
    private String description;                  //Описание
    private Boolean available;                   //доступность

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;                          //Владелец

//    @ManyToOne(fetch = FetchType.EAGER)
//    private ItemRequest request;                 //ссылка на запрос вещи
}
