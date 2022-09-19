package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;                         //Имя вещи
    private String description;                  //Описание
    @Column(name = "is_available")
    private Boolean available;                   //доступность
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;                          //Владелец
    @ManyToOne(fetch = FetchType.EAGER)
    private ItemRequest request;

}
