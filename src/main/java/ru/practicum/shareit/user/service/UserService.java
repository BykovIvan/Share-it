package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User update(Long id, User user);
    User findById(Long id);
    List<User> findAll();
    void deleteById(Long userId);
    boolean containsById(Long userId);
}
