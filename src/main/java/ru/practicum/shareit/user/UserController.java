package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту /users. Метод POST");

        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateById(@PathVariable("userId") Long userId,
                              @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту /users обновление по id. Метод PATCH");
        return userService.update(userId, userDto);
    }

    @GetMapping
    public List<UserDto> allUsers() {
        log.info("Получен запрос к эндпоинту /users получение всех. Метод GET");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto userById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users получение по id. Метод GET");
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users удаление по id. Метод DELETE");
        userService.deleteById(userId);
    }
}
