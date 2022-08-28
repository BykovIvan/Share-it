package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.*;

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
    @NotBlank
    @NonNull
    private String name;
    @NotBlank
    @NonNull
    @Size(min = 1, max = 200)
    @Email()
    private String email;
}
