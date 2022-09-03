package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long request;
    private Long ownerId;
}
