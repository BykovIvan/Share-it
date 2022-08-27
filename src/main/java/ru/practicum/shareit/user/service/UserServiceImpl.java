package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        checkUser(user);
        return repository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        checkUser(user);
        user.setUserId(id);
        // TODO Сделать проверку по ID
        return repository.save(user);
    }

    @Override
    public User findById(Long id) {
        Optional<User> userGet =  repository.findById(id);
        if (userGet.isPresent()){
            return userGet.get();
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long userId) {
        if (containsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new NotFoundException("Нет такого пользователя c ID = " + userId);
        }
    }

    @Override
    public boolean containsById(Long userId) {
        Optional<User> user = repository.findById(userId);
        return user.isPresent();
    }

    /**
     * Проверка валидации пользователей
     * Check validation users
     */
    private void checkUser(User user) {
        if (user.getEmail() != null){
            for (User getUser : repository.findAll()) {
                if (user.getEmail().equals(getUser.getEmail())) {
                    throw new ConflictException("Такой пользователь уже существует");
                }
            }
        }
    }

}
