package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    final Map<Integer, User> users;
    int id;
    Map<Integer, Set<Integer>> friends;
    ValidateService validateService;

    @Autowired
    public InMemoryUserStorage(ValidateService validateService) {
        this.validateService = validateService;
        this.users = new LinkedHashMap<>();
        this.id = 0;
        this.friends = new HashMap<>();
    }

    int createId() {
        return ++id;
    }
    @Override
    public User createUser(User user) {
        validateService.validateCreateUser(user);;

        user.setId(createId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
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

        if (friends.get(user.getId()) == null) {
            friends.put(user.getId(), new HashSet<>());
        }
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

    @Override
    public void addFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);

        Set<Integer> newUserFriends = friends.get(id);
        newUserFriends.add(friendId);
        friends.put(id, newUserFriends);

        Set<Integer> secondUserFriends = friends.get(friendId);
        secondUserFriends.add(id);
        friends.put(friendId, secondUserFriends);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        Set<Integer> newUserFriends = friends.get(id);
        newUserFriends.remove(friendId);
        friends.put(id, newUserFriends);

        Set<Integer> secondUserFriends = friends.get(friendId);
        secondUserFriends.remove(id);
        friends.put(friendId, secondUserFriends);
    }

    @Override
    public List<User> getUserFriends(int id) {
        getUserById(id);

        Set<Integer> userFriendsIds = friends.get(id);
        if (userFriendsIds == null) {
            return new ArrayList<>();
        }

        return userFriendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

}
