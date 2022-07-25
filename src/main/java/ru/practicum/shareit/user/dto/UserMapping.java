package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapping {
    public static UserDto toUserDto(User user){
        return UserDto.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto){
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
