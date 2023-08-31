package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
    }

    @Test
    public void shouldReturnEmptyListOfUsers() {
        List<User> users = controller.getUsers();

        assertEquals(new ArrayList<User>(), users);
    }

    @Test
    public void shouldReturnListOfUsers() {
        User firstUser = controller.createUser(User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());
        User secondUser = controller.createUser(User.builder()
                .id(1)
                .name("secondTestUser")
                .email("secondTest-user@ya.ru")
                .login("secondTestLogin")
                .birthday(LocalDate.of(2020, Month.DECEMBER, 3))
                .build());

        List<User> users = controller.getUsers();

        User ourFirstUser = users.get(1);
        User ourSecondUser = users.get(0);

        assertEquals(2, users.size());
        assertEquals(firstUser, ourFirstUser);
        assertEquals(secondUser, ourSecondUser);
    }

    @Test
    public void shouldCorrectlyCreateUser() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        user.setId(user.hashCode());

        User ourUser = controller.createUser(User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());

        assertEquals(user, ourUser);
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoEmail() {
        assertThrows(NullPointerException.class, () ->  User.builder()
                .id(1)
                .name("testUser")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());

    }

    @Test
    public void shouldThrowExceptionIfUserHasBlankEmail() {
        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                .id(1)
                .name("testUser")
                .email("  ")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build()));

        assertEquals("Почта не может быть пустой", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoSpecCharInEmail() {
        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                        .id(1)
                        .name("testUser")
                        .email("asdasd")
                        .login("testLogin")
                        .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                        .build()));

        assertEquals("Почта должна содержать знак @", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoLogin() {
        assertThrows(NullPointerException.class, () ->  User.builder()
                .id(1)
                .name("testUser")
                .email("testEmail@ya.ru")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());
    }

    @Test
    public void shouldThrowExceptionIfUserHasBlankLogin() {
        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                        .id(1)
                        .name("testUser")
                        .email("testEmail@ya.ru")
                        .login("   ")
                        .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                        .build()));

        assertEquals("Логин не может быть пустым", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserLoginHasSpace() {
        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                        .id(1)
                        .name("testUser")
                        .email("testEmail@ya.ru")
                        .login("bad login")
                        .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                        .build()));

        assertEquals("Логин не может содержать пробелы", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfBirthdayIsAfterThanNow() {
        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                        .id(1)
                        .name("testUser")
                        .email("testEmail@ya.ru")
                        .login("login")
                        .birthday(LocalDate.of(2025, Month.DECEMBER, 3))
                        .build()));

        assertEquals("Некорректная дата рождения", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfThisUserAlreadyExists() {
        User user = controller.createUser(User.builder()
                        .id(1)
                        .name("testUser")
                        .email("testEmail@ya.ru")
                        .login("login")
                        .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                        .build());

        Throwable throwable = assertThrows(ValidateException.class, () ->  controller
                .createUser(User.builder()
                        .id(user.getId())
                        .name("testUser")
                        .email("testEmail@ya.ru")
                        .login("login")
                        .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                        .build()));

        assertEquals("Такой пользователь уже существует", throwable.getMessage());
    }

    @Test
    public void shouldChangeNameToLoginIfNameIsEmpty() {
        User firstUser = controller.createUser(User.builder()
                .id(1)
                .name("")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());
        User secondUser = controller.createUser(User.builder()
                .id(1)
                .name("  ")
                .email("testEmail@ya.ru")
                .login("login2")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());

        assertEquals(firstUser.getName() , firstUser.getLogin());
        assertEquals(secondUser.getName(), secondUser.getLogin());
    }

    @Test
    public void shouldCorrectlyUpdateUser() {
        User firstUser = controller.createUser(User.builder()
                .id(1)
                .name("test")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build());

        User newUser = controller.updateUser(User.builder()
                .id(firstUser.getId())
                .name("updateTest")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2022, Month.DECEMBER, 3))
                .build());

        User result = controller.getUsers().get(0);

        assertEquals(result, newUser);
        assertEquals(firstUser.getId(), newUser.getId());
    }

    @Test
    public void shouldThrowExceptionIfIdOfUserIsNotFound() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.updateUser(User.builder()
                .id(1)
                .name("updateTest")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2022, Month.DECEMBER, 3))
                .build()));
        assertEquals("Такого пользователя не существует", throwable.getMessage());
    }








}