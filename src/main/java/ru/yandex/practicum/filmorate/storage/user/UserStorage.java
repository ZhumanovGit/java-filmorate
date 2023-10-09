package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    void deleteAllUsers();

    List<User> getAllUsers();

    Optional<User> getUserById(int id);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<Integer> getUserFriends(int id);
}
