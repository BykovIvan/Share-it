package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long userId;
    private String name;
    private String email;

}
