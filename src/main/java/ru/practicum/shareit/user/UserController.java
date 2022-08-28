package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту /users. Метод POST");
        User user = UserMapping.toUser(userDto);
        User userGet = userService.save(user);
        return UserMapping.toUserDto(userGet);
    }

    @PatchMapping("/{userId}")
    public UserDto updateById(@PathVariable("userId") Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту /users обновление по id. Метод PATCH");
        User user = UserMapping.toUser(userDto);
        return UserMapping.toUserDto(userService.update(userId, user));
    }

    @GetMapping
    public List<UserDto> allUsers() {
        log.info("Получен запрос к эндпоинту /users получение всех. Метод GET");
        return userService.findAll().stream()
                .map(UserMapping::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto userById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users получение по id. Метод GET");
        return UserMapping.toUserDto(userService.findById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable("userId") Long userId) {
        log.info("Получен запрос к эндпоинту /users удаление по id. Метод DELETE");
        userService.deleteById(userId);
    }
}
