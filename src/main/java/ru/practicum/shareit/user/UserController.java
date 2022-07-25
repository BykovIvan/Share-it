package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Создание пользователя
     * Create user
     */
    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO userDto) {
        log.info("Получен запрос к эндпоинту /users. Метод POST");
        User user = UserMapping.toUser(userDto);
        checkUser(user);
        return UserMapping.toUserDto(userDao.create(user));
    }

    /**
     * Обновление пользователя по его ID и body
     * Update user by id and body
     */
    @PatchMapping("/{userId}")
    public UserDTO updateById(@PathVariable("userId") Long userId,
                              @RequestBody UserDTO userDto) {
        log.info("Получен запрос к эндпоинту /users. Метод PATCH");
        User user = UserMapping.toUser(userDto);
        checkUser(user);
        return UserMapping.toUserDto(userDao.updateById(userId, user));
    }

    /**
     * Получение списка всех пользователей
     * Get list of users
     */
    @GetMapping
    public List<UserDTO> allUsers() {
        return userDao.findAllUsers().stream()
                .map(UserMapping::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение пользователя по его ID
     * Get user by ID
     */
    @GetMapping("/{userId}")
    public UserDTO userById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users. Метод GET");
        return UserMapping.toUserDto(userDao.findUserById(userId));
    }

    /**
     * Удаление пользователя по его ID
     * Delete user by ID
     */
    @DeleteMapping("/{userId}")
    public String deleteById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users. Метод DELETE");
        if (userDao.deleteUserById(userId)){
            return "Пользователь успешно удален!";
        } else {
            throw new NotFoundException("Нет такого пользователя c ID = " + userId);
        }
    }

    /**
     * Проверка валидации пользователей
     * Check validation users
     */
    private void checkUser(User user) {
        if (user.getEmail() != null){
            for (User getUser : userDao.findAllUsers()) {
                if (user.getEmail().equals(getUser.getEmail())) {
                    throw new ConflictException("Такой пользователь уже существует");
                }
            }
        }
    }
}
