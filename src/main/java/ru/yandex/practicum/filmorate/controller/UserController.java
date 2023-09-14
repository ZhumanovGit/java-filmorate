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
        log.info("Обработка зпроса с получением всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.info("Обработка запроса с получением пользователя с id = {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        log.info("Обработка запроса с созданием нового пользователя");
        return userService.createUser(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        log.info("Обработка запроса с обновлением пользователя с id = {}", user.getId());
        return userService.updateUser(user);
    }

    @DeleteMapping()
    public void deleteAll() {
        log.info("Обработка запроса с удалением всех пользователей из системы");
        userService.deleteAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        log.info("Обработка запроса с удалением пользователя с id = {}", userId);
        userService.deleteUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void makeFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Обработка запроса на добавление в друзья пользователей с id = {} и friendId = {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Обработка запроса на удаление из друзей пользователей с id = {} и friendId = {}", userId, friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Обработка запроса на получение друзей пользователя с id = {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Обработка запроса на получение общих друзей пользователей с id = {} и otherId = {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}
