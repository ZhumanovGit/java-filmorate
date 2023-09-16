package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    final Map<Integer, User> users;
    int id;
    Map<Integer, Set<Integer>> friends;

    @Autowired
    public InMemoryUserStorage() {
        this.users = new LinkedHashMap<>();
        this.id = 0;
        this.friends = new HashMap<>();
    }

    int createId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
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
       users.remove(id);
       friends.remove(id);
       for (Integer userId: friends.keySet()) {
           Set<Integer> userFriends = friends.get(userId);
           userFriends.remove(id);
       }
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        friends.clear();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(User user, User friend) {
        int userId = user.getId();
        int friendId = friend.getId();

        Set<Integer> newUserFriends = friends.get(userId);
        newUserFriends.add(friendId);
        friends.put(userId, newUserFriends);

        Set<Integer> secondUserFriends = friends.get(friendId);
        secondUserFriends.add(userId);
        friends.put(friendId, secondUserFriends);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        int userId = user.getId();
        int friendId = friend.getId();

        Set<Integer> newUserFriends = friends.get(userId);
        newUserFriends.remove(friendId);
        friends.put(userId, newUserFriends);

        Set<Integer> secondUserFriends = friends.get(friendId);
        secondUserFriends.remove(userId);
        friends.put(friendId, secondUserFriends);
    }

    @Override
    public List<Integer> getUserFriends(int id) {
        return new ArrayList<>(friends.get(id));
    }

}
