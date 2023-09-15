package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class UserStorageTest {

    UserStorage storage;

    public abstract UserStorage getStorage();

    @BeforeEach
    public void beforeEach() {
        storage = getStorage();
    }

    void assertEqualsUser(User o1, User o2) {
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getLogin(), o2.getLogin());
        assertEquals(o1.getEmail(), o2.getEmail());
        assertEquals(o1.getBirthday(), o2.getBirthday());
    }

    @Test
    public void createUser_shouldCorrectlyCreateUser_returnUser() {
        User user = User.builder()
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        User createdUser = storage.createUser(user);

        assertEqualsUser(user, createdUser);
        assertEquals(1, storage.getAllUsers().size());
    }

    @Test
    public void createUser_shouldThrowExceptionIfValidationIsFailed_ValidateException() {
        User user = User.builder()
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        assertThrows(ValidateException.class, () -> storage.createUser(user));
        assertEquals(0, storage.getAllUsers().size());
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

        User updatedUser = storage.updateUser(newUser);

        assertEquals(1, updatedUser.getId());
        assertEquals("newUser", updatedUser.getName());
    }

    @Test
    public void updateUser_shouldTrowExceptionIfNotFoundUser_NotFoundException() {
        User user = User.builder()
                .id(5)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(NotFoundException.class, () -> storage.updateUser(user));

        assertEquals("Такого пользователя не существует", throwable.getMessage());
    }

    @Test
    public void deleteUser_shouldCorrectlyRemoveUser_void() {
        User user = User.builder()
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        storage.createUser(user);

        storage.deleteUser(1);

        Throwable throwable = assertThrows(NotFoundException.class, () -> storage.getUserById(1));
        assertEquals("Такого пользователя не существует", throwable.getMessage());
    }

    @Test
    public void deleteUser_shouldThrowExceptionIfUserWasNotFound_NotFoundException() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> storage.deleteUser(1));

        assertEquals("Такого пользователя не существует", throwable.getMessage());
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

        List<User> users = storage.getAllUsers();

        assertEquals(3, users.size());
        assertEqualsUser(users.get(0), firstUser);
        assertEqualsUser(users.get(1), secondUser);
        assertEqualsUser(users.get(2), thirdUser);
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

        User ourUser = storage.getUserById(createdUser.getId());

        assertEqualsUser(createdUser, ourUser);
    }

    @Test
    public void getUserById_shouldThrowExceptionIfUserWasNotFound_NotFoundException() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> storage.getUserById(15));

        assertEquals("Такого пользователя не существует", throwable.getMessage());
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

        storage.addFriend(firstCreatedUser.getId(), secondCreatedUser.getId());

        assertEquals(List.of(secondCreatedUser), storage.getUserFriends(1));
        assertEquals(List.of(firstCreatedUser), storage.getUserFriends(2));
    }

    @Test
    public void addFriend_shouldThrowExceptionIfCantFindUserOrFriend_NotFoundException() {
        User firstUser = User.builder()
                .name("firstUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .id(2)
                .name("secondUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User firstCreatedUser = storage.createUser(firstUser);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> storage.addFriend(firstCreatedUser.getId(), secondUser.getId()));

        assertEquals("Такого пользователя не существует", throwable.getMessage());
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
        storage.addFriend(firstCreatedUser.getId(), secondCreatedUser.getId());

        storage.deleteFriend(firstCreatedUser.getId(), secondCreatedUser.getId());

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

        List<User> friends = storage.getUserFriends(firstCreatedUser.getId());

        assertEquals(new ArrayList<>(), friends);
    }

}