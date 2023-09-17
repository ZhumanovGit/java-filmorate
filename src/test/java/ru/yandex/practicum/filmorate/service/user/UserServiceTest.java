package ru.yandex.practicum.filmorate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserStorage storage;
    @InjectMocks
    UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService = new UserService(storage, new ValidateService());
    }

    void assertEqualsUser(User o1, User o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getLogin(), o2.getLogin());
        assertEquals(o1.getEmail(), o2.getEmail());
        assertEquals(o1.getBirthday(), o2.getBirthday());
    }

    @Test
    public void getAll_whenCalled_thenReturnedListOfUsers() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .id(2)
                .name("testUser2")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User thirdUser = User.builder()
                .id(3)
                .name("testUser3")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        when(storage.getAllUsers()).thenReturn(List.of(user, secondUser, thirdUser));
        List<User> expectedUsers = List.of(user, secondUser, thirdUser);

        List<User> actualUsers = userService.getAll();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void getAll_whenZeroUsers_thenReturnedEmptyList() {
        when(storage.getAllUsers()).thenReturn(new ArrayList<>());
        List<User> expectedUsers = new ArrayList<>();

        List<User> actualUsers = userService.getAll();

        assertEquals(expectedUsers, actualUsers);
        assertEquals(0, actualUsers.size());
    }

    @Test
    public void getUserById_whenUserWasFound_thenReturnedUser() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        when(storage.getUserById(1)).thenReturn(Optional.of(user));

        User actualUser = userService.getUserById(1);

        assertEqualsUser(user, actualUser);
    }

    @Test
    public void getUserById_whenUserWasNotFound_thenThrowException() {
        when(storage.getUserById(1)).thenReturn(Optional.empty());
        String expectedResponse = "Такого пользователя не существует";

        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.getUserById(1));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createUser_whenValidationWasSuccess_thenReturnedUser() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        when(storage.createUser(user)).thenReturn(user);

        User actualUser = userService.createUser(user);

        assertEqualsUser(user, actualUser);
    }

    @Test
    public void createUser_whenValidationWasNotSuccess_thenThrowException() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        String expectedResponse = "Пользователь не имеет логин";

        Throwable throwable = assertThrows(ValidateException.class, () -> userService.createUser(user));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUserFriends_whenUserHasNoFriends_thenReturnedEmptyList() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("newLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        userService.createUser(user);
        List<User> expectedFriends = new ArrayList<>();
        when(storage.getUserById(1)).thenReturn(Optional.of(user));

        List<User> actualList = userService.getUserFriends(1);

        assertEquals(expectedFriends, actualList);
    }

    @Test
    public void getUserFriends_whenUserHasFriends_thenReturnedListOfUsers() {
        User user = User.builder()
                .id(1)
                .name("testUser")
                .email("test-user@ya.ru")
                .login("newLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User secondUser = User.builder()
                .id(2)
                .name("testUser2")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        User thirdUser = User.builder()
                .id(3)
                .name("testUser3")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();
        userService.createUser(user);
        userService.createUser(secondUser);
        userService.createUser(thirdUser);
        when(storage.getUserById(1)).thenReturn(Optional.of(user));
        when(storage.getUserById(2)).thenReturn(Optional.of(secondUser));
        when(storage.getUserById(3)).thenReturn(Optional.of(thirdUser));
        userService.addFriend(1, 2);
        userService.addFriend(1, 3);
        List<User> expectedFriends = List.of(secondUser, thirdUser);
        when(storage.getUserFriends(1)).thenReturn(List.of(2, 3));

        List<User> actualList = userService.getUserFriends(1);

        assertEquals(expectedFriends, actualList);
    }



}