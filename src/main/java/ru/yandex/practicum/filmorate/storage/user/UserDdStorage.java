package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDdStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("viewers")
                .usingGeneratedKeyColumns("viewer_id");

        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE viewers SET email = ?, login = ?, viewer_name = ?, birthday = ?" +
                "WHERE viewer_id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        String sqlQuery = "DELETE FROM viewers WHERE viewer_id = ?";
        jdbcTemplate.update(sqlQuery, id);

    }

    @Override
    public void deleteAllUsers() {
        String sqlQuery = "DELETE FROM viewers";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM viewers";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(int id) {
        User user;
        try {
            String sqlQuery = "SELECT * FROM viewers WHERE viewer_id = ?";
            user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (Exception exp) {
            return Optional.empty();
        }
        return Optional.of(user);

    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO friendships (viewer_id, friend_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM friendships WHERE viewer_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public List<Integer> getUserFriends(int id) {
        String sqlQuery = "SELECT * FROM friendships WHERE viewer_id = ?";
        List<Friendship> friendships = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFriendship(rs), id);

        return friendships.stream()
                .map(Friendship::getFriend)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("viewer_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("viewer_name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        Optional<User> user = getUserById(rs.getInt("viewer_id"));
        Optional<User> friend = getUserById(rs.getInt("friend_id"));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        return new Friendship(user.get(), friend.get());
    }
}
