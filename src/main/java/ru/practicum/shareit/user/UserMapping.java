package ru.practicum.shareit.user;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class UserMapping {
    public static UserDto toUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
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
