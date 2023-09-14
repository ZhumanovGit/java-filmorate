package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    final Map<Integer, User> users;
    int id;
    ValidateService validateService;

    @Autowired
    public InMemoryUserStorage(ValidateService validateService) {
        this.validateService = validateService;
        this.users = new LinkedHashMap<>();
        this.id = 0;
    }

    int createId() {
        return ++id;
    }
    @Override
    public User createUser(User user) {
        validateService.validateCreateUser(user);

        user.setId(createId());

        user.setFriends(new HashSet<>());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateService.validateUpdateUser(user);

        if (users.get(user.getId()) == null) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (users.remove(id) == null) {
            throw new NotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        return user;
    }
}
