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
        System.out.println(firstUser);
        User secondUser = getUserById(friendId);
        System.out.println(secondUser);

        firstUser.getFriends().add((long) friendId);
        System.out.println("im working");
        secondUser.getFriends().add((long) id);
        System.out.println("im working");

        System.out.println("im done");

    }

    public void deleteFriend(int id, int friendId) {
        User firstUser = getUserById(id);
        User secondUser = getUserById(friendId);

        firstUser.getFriends().remove((long) friendId);
        secondUser.getFriends().remove((long) id);
    }

    public List<Long> getUserFriends(int id) {
        User user = getUserById(id);
        return new ArrayList<>(user.getFriends());
    }

    public List<Long> getMutualFriends(int id, int friendId) {
        Set<Long> firstSet = getUserById(id).getFriends();
        Set<Long> secondSet = getUserById(friendId).getFriends();

        List<Long> result = firstSet.stream()
                .filter(secondSet::contains)
                .collect(Collectors.toList());

        log.info("количство общих друзей {}", result.size());

        return result;
    }
}
