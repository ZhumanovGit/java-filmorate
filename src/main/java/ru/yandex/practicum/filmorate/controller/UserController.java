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
        List<User> users = userService.getAll();
        log.info("Получен список пользователей");
        return users;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.info("Обработка запроса с получением пользователя с id = {}", userId);
        User user = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", userId);
        return user;
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        log.info("Обработка запроса с созданием нового пользователя");
        User newUser = userService.createUser(user);
        int newUserId = newUser.getId();
        log.info("Создан новый пользователь с id = {}", newUserId);
        return newUser;
    }

    @PutMapping()
    public void updateUser(@RequestBody User user) {
        int userId = user.getId();
        log.info("Обработка запроса с обновлением пользователя с id = {}", user.getId());
        userService.updateUser(user);
        log.info("Обновлен пользователь с id = {}", userId);
    }

    @DeleteMapping()
    public void deleteAll() {
        log.info("Обработка запроса с удалением всех пользователей из системы");
        userService.deleteAll();
        log.info("Все пользователи удалены");
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        log.info("Обработка запроса с удалением пользователя с id = {}", userId);
        userService.deleteUserById(userId);
        log.info("Пользователь с id = {} удален", userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void makeFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Обработка запроса на добавление в друзья пользователей с id = {} и friendId = {}", userId, friendId);
        userService.addFriend(userId, friendId);
        log.info("Пользователи с id = {} и id = {} добавлены в друзья друг к другу", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Обработка запроса на удаление из друзей пользователей с id = {} и friendId = {}", userId, friendId);
        userService.deleteFriend(userId, friendId);
        log.info("Пользователи с id = {} и id = {} удалены из друзей друг друга", userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Обработка запроса на получение друзей пользователя с id = {}", id);
        List<User> friends = userService.getUserFriends(id);
        log.info("Получены друзья пользователя с id = {}", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Обработка запроса на получение общих друзей пользователей с id = {} и otherId = {}", id, otherId);
        List<User> mutualFriends = userService.getMutualFriends(id, otherId);
        log.info("Получены общие друзья пользователей с id = {} и id = {}", id, otherId);
        return mutualFriends;
    }
}
