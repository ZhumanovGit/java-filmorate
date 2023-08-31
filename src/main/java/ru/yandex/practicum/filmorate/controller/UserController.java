package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        validateUser(user);

        if (users.get(user.getId()) != null) {
            log.warn("Исключение ValidateException в POST запросе, Такой пользователь уже существует");
            throw new ValidateException("Такой пользователь уже существует");
        }

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(user.hashCode());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} создан и добавлен в систему", user.getId());
        return user;
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User user) {
        validateUser(user);

        if (users.get(user.getId()) == null) {
            log.warn("Исключение ValidateException в PUT запросе, Такого пользователя не существует");
            throw new ValidateException("Такого пользователя не существует");
        }

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id = {} обновлен", user.getId());
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.warn("Исключение ValidateException, Почта пользователя пустая");
            throw new ValidateException("Почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Исключение ValidateException, Почта не содержит @");
            throw new ValidateException("Почта должна содержать знак @");
        }

        if (user.getLogin().isBlank()) {
            log.warn("Исключение ValidateException, Логин пустой");
            throw new ValidateException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Исключение ValidateException, Логин содержит пробелы");
            throw new ValidateException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Исключение ValidateException, Некорректная дата рождения");
            throw new ValidateException("Некорректная дата рождения");
        }
    }


}
