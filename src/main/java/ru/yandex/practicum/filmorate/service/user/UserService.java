package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage storage;
    ValidateService validateService;

    @Autowired
    public UserService(UserStorage storage, ValidateService validateService) {
        this.storage = storage;
        this.validateService = validateService;
    }

    public List<User> getAll() {
        return storage.getAllUsers();
    }

    public User getUserById(int id) {
        Optional<User> mayBeUser = storage.getUserById(id);

        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        return mayBeUser.get();
    }

    public User createUser(User user) {
        validateService.validateCreateUser(user);

        return storage.createUser(user);
    }

    public void updateUser(User user) {
        validateService.validateUpdateUser(user);
        int userId = user.getId();
        getUserById(userId);

        storage.updateUser(user);
    }

    public void deleteAll() {
        storage.deleteAllUsers();
    }

    public void deleteUserById(int id) {
        storage.deleteUser(id);
    }

    public void addFriend(int id, int friendId) {
        User firstUser = getUserById(id);
        User secondUser = getUserById(friendId);

        storage.addFriend(firstUser, secondUser);
    }

    public void deleteFriend(int id, int friendId) {
        User firstUser = getUserById(id);
        User secondUser = getUserById(friendId);

        storage.deleteFriend(firstUser, secondUser);

    }

    public List<User> getUserFriends(int id) {
        getUserById(id);

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
            return new ArrayList<>();
        }

        return firstList.stream()
                .filter(secondList::contains)
                .collect(Collectors.toList());
    }
}
