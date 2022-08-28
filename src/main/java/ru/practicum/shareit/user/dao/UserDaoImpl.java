package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class UserDaoImpl implements UserDao {
    private Long userId = 1L;
    private final Map<Long, User> mapUsers = new HashMap<>();

    @Override
    public User create(User user) {
        user.setUserId(userId++);
        mapUsers.put(user.getUserId(), user);
        return mapUsers.get(user.getUserId());
    }

    @Override
    public User updateById(Long userId, User user) {
        if (!mapUsers.containsKey(userId)) {
            throw new NotFoundException("Нет такого пользователя c ID " + userId);
        }
        User getUser = mapUsers.get(userId);
        if (user.getName() != null){
            getUser.setName(user.getName());
        }
        if (user.getEmail() != null){
            getUser.setEmail(user.getEmail());
        }
        mapUsers.put(userId, getUser);
        return mapUsers.get(userId);


    }

    @Override
    public User findUserById(Long userId) {
        if (!mapUsers.containsKey(userId)) {
            throw new NotFoundException("Нет такого пользователя c ID " + userId);
        }
        return mapUsers.get(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(mapUsers.values());
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (mapUsers.containsKey(userId)) {
            mapUsers.remove(userId);
            return true;
        }
        return false;

    }

    @Override
    public boolean containsUserById(Long userId) {
        return mapUsers.containsKey(userId);
    }

}

