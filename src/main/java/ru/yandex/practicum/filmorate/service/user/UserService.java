package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    final UserStorage storage;
    final ValidateService validateService;

    public List<User> getAll() {
        return storage.getAllUsers();
    }

    public User getUserById(int id) {
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
    }

    public User createUser(User user) {
        validateService.validateCreateUser(user);

        return storage.createUser(user);
    }

    public void updateUser(User user) {
        validateService.validateUpdateUser(user);
        int userId = user.getId();
        storage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        storage.updateUser(user);
    }

    public void deleteAll() {
        storage.deleteAllUsers();
    }

    public void deleteUserById(int id) {
        storage.deleteUser(id);
    }

    public void addFriend(int id, int friendId) {
        User firstUser = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        User secondUser = storage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        storage.addFriend(firstUser, secondUser);
    }

    public void deleteFriend(int id, int friendId) {
        User firstUser = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        User secondUser = storage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        storage.deleteFriend(firstUser, secondUser);

    }

    public List<User> getUserFriends(int id) {
        storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        List<Integer> userFriendsIds = storage.getUserFriends(id);
        if (userFriendsIds == null) {
            return new ArrayList<>();
        }

        return userFriendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(int id, int friendId) {
        List<User> firstList = getUserFriends(id);
        List<User> secondList = getUserFriends(friendId);

        if (firstList.isEmpty() || secondList.isEmpty()) {
            return Collections.emptyList();
        }

        return firstList.stream()
                .filter(secondList::contains)
                .collect(Collectors.toList());
    }
}
