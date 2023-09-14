package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получены все пользователи");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.info("Получен пользователь с id = {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        log.info("Пользователь с id = {} обновлен", user.getId());
        return userService.updateUser(user);
    }

    @DeleteMapping()
    public void deleteAll() {
        log.info("Все пользователи удалены из системы");
        userService.deleteAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        log.info("Пользователь с id = {}", userId);
        userService.deleteUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void makeFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Пользователи с id = {} и friendId = {} теперь друзья", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Пользователи с id = {} и friendId = {} больше не друзья", userId, friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<Long> getFriends(@PathVariable int id) {
        log.info("Получены друзья пользователя с id = {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Long> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получены общие друзья пользователей с id = {} и id = {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}
