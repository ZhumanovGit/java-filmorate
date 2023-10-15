package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public List<User> getAll() {
        return storage.getAllUsers();
    }

    public User getUserById(int id) {
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
    }

    public User createUser(User user) {
        validateCreateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return storage.createUser(user);
    }

    public void updateUser(User user) {
        validateUpdateUser(user);
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

    private void validateCreateUser(User user) {
        if (user.getEmail() == null) {
            log.warn("ValidationException, Почта null");
            throw new ValidateException("Пользователь не имеет почту");
        }

        if (user.getEmail().isBlank()) {
            log.warn("ValidationException, Почта пустая");
            throw new ValidateException("Почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("ValidationException, Почта не содержит @");
            throw new ValidateException("Почта должна содержать знак @");
        }

        if (user.getLogin() == null) {
            log.warn("ValidationException, Логин null");
            throw new ValidateException("Пользователь не имеет логин");
        }

        if (user.getLogin().isBlank()) {
            log.warn("ValidationException, Логин пустой");
            throw new ValidateException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("ValidationException, Логин содержит пробелы");
            throw new ValidateException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("ValidationException, Некорректная дата");
            throw new ValidateException("Некорректная дата рождения");
        }
    }

    private void validateUpdateUser(User user) {
        validateCreateUser(user);

        if (user.getId() == null) {
            log.warn("ValidationException, не передан id пользователя");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }
}
