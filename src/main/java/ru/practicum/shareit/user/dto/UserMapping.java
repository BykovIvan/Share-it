package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapping {
    public static UserDTO toUserDto(User user){
        return UserDTO.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDTO userDto){
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
