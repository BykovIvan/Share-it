package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto create(User user);
    UserDto update(Long id, UserDto userDto);
    UserDto findById(Long id);
    List<UserDto> findAll();
    void deleteById(Long userId);
}
