package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public List<User> getAll() {
        return storage.getAllUsers();
    }

    public User getUserById(int id) {
        return storage.getUserById(id);
    }

    public User createUser(User user) {
        User newUser = storage.createUser(user);
        log.info("Пользователь с id = {} создан и добавлен в систему", newUser.getId());
        return newUser;
    }

    public User updateUser(User user) {
        log.info("Пользователь с id = {} обновлен", user.getId());
        return storage.updateUser(user);
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

        firstUser.getFriends().add((long) friendId);
        secondUser.getFriends().add((long) id);
    }

    public void deleteFriend(int id, int friendId) {
        User firstUser = getUserById(id);
        User secondUser = getUserById(friendId);

        firstUser.getFriends().remove((long) friendId);
        secondUser.getFriends().remove((long) id);
    }

    public List<User> getUserFriends(int id) {
        Set<Long> friends= getUserById(id).getFriends();

        if (friends == null) {
            return new ArrayList<>();
        }

        return friends.stream()
                .map(friendId -> getUserById(friendId.intValue()))
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
