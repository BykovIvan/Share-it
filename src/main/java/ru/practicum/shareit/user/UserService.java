package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto save(User user);
    UserDto update(Long id, UserDto userDto);
    UserDto findById(Long id);
    List<UserDto> findAll();
    void deleteById(Long userId);
    boolean containsById(Long userId);
}
