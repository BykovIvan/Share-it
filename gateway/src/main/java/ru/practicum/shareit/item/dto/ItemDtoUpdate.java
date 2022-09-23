package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoUpdate {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private Long ownerId;
}
