package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User save(User user);
    User update(Long id, UserDto userDto);
    User findById(Long id);
    List<User> findAll();
    void deleteById(Long userId);
    boolean containsById(Long userId);
}
