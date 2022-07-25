package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    /**
     * Добавление пользователя в локальное хранилище
     * Adding the User to local storage
     */
    User create(User user);

    /**
     * Обновление пользователя в локальном хранилище
     * Update the user in local storage
     */
    User updateById(Long userId, User user);

    /**
     * Поиск пользователей по Id
     * Get user by ID
     */
    User findUserById(Long userId);

    /**
     * Получение всех пользователей из хранилища
     * Getting all users from local storage
     */
    List<User> findAllUsers();

    /**
     * Удаление пользователя из локального хранилища
     * Remove the user from local storage
     */
    boolean deleteUserById(Long userId);

    /**
     * Провека есть ли в базе пользователь
     * Check user in db
     */

    boolean containsUserById(Long userId);
}
