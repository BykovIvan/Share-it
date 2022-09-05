package ru.practicum.shareit.user;

import lombok.*;

/**
 * Класс который возвращается полльзователям
 * The class that is returned to users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
