package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryUserStorageTest {

    InMemoryUserStorage storage;

    @BeforeEach
    public void beforeEach() {
        this.storage = new InMemoryUserStorage();
    }

    void assertEqualsUser(User o1, User o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getLogin(), o2.getLogin());
        assertEquals(o1.getEmail(), o2.getEmail());
        assertEquals(o1.getBirthday(), o2.getBirthday());
    }

    @Test
    public void createUser_shouldCorrectlyCreateUser_returnUser() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        Map<Integer, User> expectedUserMap = new LinkedHashMap<>();
        expectedUserMap.put(1, user);
        Map<Integer, Set<Integer>> expectedFriendsMap = new LinkedHashMap<>();
        expectedFriendsMap.put(1, new HashSet<>());

        User createdUser = storage.createUser(user);

        assertEqualsUser(user, createdUser);
        assertEquals(1, storage.getAllUsers().size());
        assertEquals(expectedUserMap, storage.users);
        assertEquals(expectedFriendsMap, storage.friends);
    }


    @Test
    public void updateUser_shouldCorrectlyUpdateUser_returnUser() {
        User user = User.builder()
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User newUser = User.builder()
                .id(1)
                .name("newUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        storage.createUser(user);
        Map<Integer, User> expectedUserMap = new LinkedHashMap<>();
        expectedUserMap.put(1, newUser);

        User updatedUser = storage.updateUser(newUser);

        assertEquals(1, updatedUser.getId());
        assertEquals("newUser", updatedUser.getName());
        assertEquals(expectedUserMap, storage.users);
    }


    @Test
    public void deleteAllUsers_shouldDeleteAllUsers_void() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .name("secondUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User thirdUser = User.builder()
                .name("thirdUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        storage.createUser(firstUser);
        storage.createUser(secondUser);
        storage.createUser(thirdUser);

        storage.deleteAllUsers();

        assertEquals(0, storage.getAllUsers().size());
        assertEquals(0, storage.friends.size());
    }

    @Test
    public void getAllUsers_shouldReturnAllUsers_ListOfUsers() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .name("secondUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User thirdUser = User.builder()
                .name("thirdUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        storage.createUser(firstUser);
        storage.createUser(secondUser);
        storage.createUser(thirdUser);
        List<User> expectedUserList = new ArrayList<>();
        expectedUserList.add(firstUser);
        expectedUserList.add(secondUser);
        expectedUserList.add(thirdUser);

        List<User> users = storage.getAllUsers();

        assertEquals(3, users.size());
        assertEqualsUser(users.get(0), firstUser);
        assertEqualsUser(users.get(1), secondUser);
        assertEqualsUser(users.get(2), thirdUser);
        assertEquals(expectedUserList, storage.getAllUsers());
    }

    @Test
    public void getUserById_shouldCorrectlyFindUser_returnUser() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User createdUser = storage.createUser(firstUser);

        Optional<User> ourUser = storage.getUserById(createdUser.getId());

        assertEqualsUser(createdUser, ourUser.get());
    }

    @Test
    public void getUserById_whenUserIsNull_returnEmptyOptional() {

        Optional<User> ourUser = storage.getUserById(5);

        assertEquals(true, ourUser.isEmpty());
    }

    @Test
    public void addFriend_shouldCorrectlyAddFriends_void() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .name("secondUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User firstCreatedUser = storage.createUser(firstUser);
        User secondCreatedUser = storage.createUser(secondUser);

        storage.addFriend(firstCreatedUser, secondCreatedUser);

        assertEquals(List.of(secondCreatedUser.getId()), storage.getUserFriends(1));
        assertEquals(List.of(firstCreatedUser.getId()), storage.getUserFriends(2));
    }

    @Test
    public void deleteFriend_shouldCorrectlyDeleteFriends_void() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .name("secondUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User firstCreatedUser = storage.createUser(firstUser);
        User secondCreatedUser = storage.createUser(secondUser);
        storage.addFriend(firstCreatedUser, secondCreatedUser);

        storage.deleteFriend(firstCreatedUser, secondCreatedUser);

        assertEquals(new ArrayList<>(), storage.getUserFriends(1));
        assertEquals(new ArrayList<>(), storage.getUserFriends(2));
    }

    @Test
    public void getUserFriends_shouldReturnNewArrayListIfUserHasNoFriends_returnNewList() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User firstCreatedUser = storage.createUser(firstUser);

        List<Integer> friends = storage.getUserFriends(firstCreatedUser.getId());

        assertEquals(new ArrayList<>(), friends);
    }

}
