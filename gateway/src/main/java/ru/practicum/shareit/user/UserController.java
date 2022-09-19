package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> safeUser(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user");
        return userClient.safeUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateById(@Positive @PathVariable("userId") Long userId,
                                             @RequestBody @Valid UserDtoUpdate userDto) {
        log.info("Update user with id = {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> allUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> userById(@Positive @PathVariable("userId") Long userId) {
        log.info("Get user with id = {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@Positive @PathVariable("userId") Long userId) {
        log.info("Delete user with id = {}", userId);
        return userClient.deleteUser(userId);
    }
}
