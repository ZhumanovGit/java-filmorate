package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    final UserStorage storage;
    final ValidationService validationService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage, ValidationService validationService) {
        this.storage = storage;
        this.validationService = validationService;
    }

    public List<User> getAll() {
        return storage.getAllUsers();
    }

    public User getUserById(int id) {
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
    }

    public User createUser(User user) {
        validationService.validateCreateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return storage.createUser(user);
    }

    public void updateUser(User user) {
        validationService.validateUpdateUser(user);
        int userId = user.getId();
        storage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        storage.updateUser(user);
    }

    public void deleteAll() {
        storage.deleteAllUsers();
    }

    public void deleteUserById(int id) {
        storage.deleteUser(id);
    }

    public void addFriend(int id, int friendId) {
        User user = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        User friend = storage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        storage.addFriend(user, friend);
    }

    public void deleteFriend(int id, int friendId) {
        User user = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        User friend = storage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        storage.deleteFriend(user, friend);

    }

    public List<User> getUserFriends(int id) {
        storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        List<Integer> userFriendsIds = storage.getUserFriends(id);
        if (userFriendsIds == null) {
            return new ArrayList<>();
        }

        return userFriendsIds.stream()
                .map(storage::getUserById)
                .map(mayBeUser -> mayBeUser
                        .orElseThrow(() -> new NotFoundException("Такого пользователя не существует")))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(int id, int friendId) {
        User user = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        User friend = storage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        return storage.getMutualFriends(user, friend);

    }
}
