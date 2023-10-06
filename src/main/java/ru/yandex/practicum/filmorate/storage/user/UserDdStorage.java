package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDdStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(int id) {

    }

    @Override
    public void deleteAllUsers() {

    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from viewers";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from viewers where id = ?", id);

        if (userRows.next()) {
            User user = new User(
                    userRows.getInt("viewer_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("viewer_name"),
                    userRows.getDate("birthday").toLocalDate()
            );

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(User user, User friend) {

    }

    @Override
    public void deleteFriend(User user, User friend) {

    }

    @Override
    public List<Integer> getUserFriends(int id) {
        return null;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("viewer_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("viewer_name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }
}
