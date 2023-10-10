package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    public void createUser_whenUserIsCorrect_thanCreateAndReturnUser() {
        User user = User.builder()
                .login("niceOleg1")
                .email("niceOleg@ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build();

        User createdUser = userDbStorage.createUser(user);
        int userId = user.getId();

        assertThat(createdUser)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("login", "niceOleg1")
                .hasFieldOrPropertyWithValue("email", "niceOleg@ru")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2020, 12, 1))
                .hasFieldOrPropertyWithValue("name", "Oleg");
    }

    @Test
    public void updateUser_whenUserIsCorrectAndHasGenres_returnUpdatedUserWithGenres() {
        User user = userDbStorage.createUser(User.builder()
                .login("niceOleg2")
                .email("niceOleg@gg.com")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        int oldFilmId = user.getId();
        User newUser = User.builder()
                .id(oldFilmId)
                .login("neNiceOleg3")
                .email("niceOleg@ggcom")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build();

        userDbStorage.updateUser(newUser);

        assertThat(newUser)
                .hasFieldOrPropertyWithValue("id", oldFilmId)
                .hasFieldOrPropertyWithValue("login", "neNiceOleg3")
                .hasFieldOrPropertyWithValue("email", "niceOleg@ggcom")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2020, 12, 1))
                .hasFieldOrPropertyWithValue("name", "Oleg");
    }

    @Test
    public void deleteUser_whenCalled_thanDeleteUserFromDatabase() {
        User user = userDbStorage.createUser(User.builder()
                .login("niceOleg4")
                .email("niceOleg@gg")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        int userId = user.getId();

        userDbStorage.deleteUser(userId);
        Optional<User> mayBeUser = userDbStorage.getUserById(userId);

        assertThat(mayBeUser).isEmpty();
    }

    @Test
    public void deleteAllUsers_whenCalled_thanDeleteAllUserFromDatabase() {
        User user = userDbStorage.createUser(User.builder()
                .login("niceOleg")
                .email("niceOleg@g.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neNiceOleg")
                .email("neNiceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        User thirdUser = userDbStorage.createUser(User.builder()
                .login("voobcheNeNiceOleg")
                .email("voobcheNeniceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("voobcheNeOleg")
                .build());

        userDbStorage.deleteAllUsers();
        List<User> users = userDbStorage.getAllUsers();

        assertThat(users).isEmpty();
    }

    @Test
    public void getAllUsers_whenDatabaseHasNoUsers_thanReturnEmptyList() {
        userDbStorage.deleteAllUsers();

        List<User> users = userDbStorage.getAllUsers();

        assertThat(users).isEmpty();
    }

    @Test
    public void getAllUsers_whenDatabaseHasUsers_thanReturnListOfUsers() {
        User user = userDbStorage.createUser(User.builder()
                .login("niceOleg5")
                .email("niceOle@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neNiceOleg6")
                .email("neNiceOle@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        User thirdUser = userDbStorage.createUser(User.builder()
                .login("voobcheNeNiceOleg9")
                .email("voobcheNeniceOle@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("voobcheNeOleg")
                .build());

        List<User> users = userDbStorage.getAllUsers();

        assertThat(users).isNotEmpty();
        assertThat(user).isIn(users);
        assertThat(secondUser).isIn(users);
        assertThat(thirdUser).isIn(users);
    }


    @Test
    public void getUserById_whenUserWasFound_returnOptionalWithUser() {
        User newUser = userDbStorage.createUser(User.builder()
                .login("niceOleg43")
                .email("nicOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        int newUserId = newUser.getId();

        Optional<User> userOptional = userDbStorage.getUserById(newUserId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id",newUserId)
                                .hasFieldOrPropertyWithValue("name", "Oleg")
                                .hasFieldOrPropertyWithValue("login", "niceOleg43")
                                .hasFieldOrPropertyWithValue("email", "nicOleg@gg.ru")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2020, 12, 1))
                                );

    }

    @Test
    public void getUserById_whenUserWasNotFound_returnEmptyOptional() {
        Optional<User> userOptional = userDbStorage.getUserById(1);

        assertThat(userOptional).isEmpty();
    }

    @Test
    public void addFriend_whenUserSendRequestAndSecondUserDoNotApprovedIt_thanAddOnlySecondUserToUsersFriendList() {
        User user = userDbStorage.createUser(User.builder()
                .login("nice0leg")
                .email("nceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neNice0leg")
                .email("neiceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        int userId = user.getId();
        int friendId = secondUser.getId();

        userDbStorage.addFriend(user, secondUser);
        List<Integer> userFriends = userDbStorage.getUserFriends(userId);
        List<Integer> friendFriends = userDbStorage.getUserFriends(friendId);

        assertThat(friendId).isIn(userFriends);
        assertThat(userId).isNotIn(friendFriends);
    }

    @Test
    public void deleteFriend_whenCalled_thanDeleteFriendOnlyFromUsersListOfFriends() {
        User user = userDbStorage.createUser(User.builder()
                .login("nice01eg")
                .email("niceleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neNice01eg")
                .email("eNiceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        int userId = user.getId();
        int friendId = secondUser.getId();
        userDbStorage.addFriend(user, secondUser);
        userDbStorage.addFriend(secondUser, user);

        userDbStorage.deleteFriend(user, secondUser);
        List<Integer> userFriends = userDbStorage.getUserFriends(userId);
        List<Integer> friendFriends = userDbStorage.getUserFriends(friendId);

        assertThat(userId).isIn(friendFriends);
        assertThat(friendId).isNotIn(userFriends);
    }

    @Test
    public void getUserFriends_whenUserHasFriends_thanReturnListOfFriendsIds() {
        User user = userDbStorage.createUser(User.builder()
                .login("n1ceOleg")
                .email("niceOleg@gg.r")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neN1ceOleg")
                .email("neNiceOleg@gg.u")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        int userId = user.getId();
        int friendId = secondUser.getId();
        userDbStorage.addFriend(user, secondUser);

        List<Integer> userFriends = userDbStorage.getUserFriends(userId);

        assertThat(userFriends).isNotEmpty();
        assertThat(friendId).isIn(userFriends);
    }

    @Test
    public void getUserFriends_whenUserHasNoFriends_thanReturnEmptyList() {
        User user = userDbStorage.createUser(User.builder()
                .login("n1ce0leg")
                .email("niceOleg@ggg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Oleg")
                .build());
        User secondUser = userDbStorage.createUser(User.builder()
                .login("neNiceOleg")
                .email("neNiceOleg@ggg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("neOleg")
                .build());
        int userId = user.getId();

        List<Integer> userFriends = userDbStorage.getUserFriends(userId);

        assertThat(userFriends).isEmpty();
    }
}