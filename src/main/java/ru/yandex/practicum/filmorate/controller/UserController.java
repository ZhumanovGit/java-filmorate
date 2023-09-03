package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new LinkedHashMap<>();

    private int id = 0;

    private int createId() {
        return ++id;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {

        ValidateService.validateCreateUser(user);

        user.setId(createId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id = {} создан и добавлен в систему", user.getId());
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        ValidateService.validateUpdateUser(user);

        if (users.get(user.getId()) == null) {
            log.warn("Исключение NotFoundException в PUT запросе, Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id = {} обновлен", user.getId());
        return user;
    }
}
