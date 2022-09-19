package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @NotNull
//    @NotBlank
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;
//    @NotNull
    private Timestamp created;
}
