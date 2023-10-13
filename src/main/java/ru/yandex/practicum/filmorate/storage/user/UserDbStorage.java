package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcOperations operations;

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO viewers " +
                "(email, login, viewer_name, birthday) " +
                "VALUES (:email, :login, :name, :birthday)";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());

        operations.update(sqlQuery, params, keyHolder);

        int userId = (int) keyHolder.getKey();
        user.setId(userId);
        return user;
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery = "UPDATE viewers SET email = :email, login = :login, viewer_name = :name, birthday = :birthday " +
                "WHERE viewer_id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", user.getId())
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday())
                .addValue("login", user.getLogin());

        operations.update(sqlQuery, params);
    }

    @Override
    public void deleteUser(int id) {
        String sqlQueryForFriendship = "DELETE FROM friendships WHERE viewer_id = :id OR friend_id = :id";
        SqlParameterSource needId = new MapSqlParameterSource("id", id);
        operations.update(sqlQueryForFriendship, needId);

        String sqlQuery = "DELETE FROM viewers WHERE viewer_id = :id";
        operations.update(sqlQuery, needId);


    }

    @Override
    public void deleteAllUsers() {
        operations.getJdbcOperations().update("DELETE FROM friendships");

        operations.getJdbcOperations().update("DELETE FROM viewers");
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM viewers";
        return operations.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(int id) {
        User user;
        try {
            String sqlQuery = "SELECT * FROM viewers WHERE viewer_id = ?";
            user = operations.getJdbcOperations().queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (DataAccessException exp) {
            return Optional.empty();
        }
        return Optional.of(user);

    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO friendships (viewer_id, friend_id)" +
                "VALUES (:userId, :friendId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", user.getId())
                .addValue("friendId", friend.getId());
        operations.update(sqlQuery, params);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM friendships WHERE viewer_id = :userId AND friend_id = :friendId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", user.getId())
                .addValue("friendId", friend.getId());
        operations.update(sqlQuery, params);
    }

    @Override
    public List<Integer> getUserFriends(int id) {
        String sqlQuery = "SELECT * FROM friendships WHERE viewer_id = ?";
        List<FriendShip> friendShips = operations.getJdbcOperations().query(sqlQuery, (rs, rowNum) -> makeFriendship(rs), id);

        return friendShips.stream()
                .map(FriendShip::getFriend)
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

    private FriendShip makeFriendship(ResultSet rs) throws SQLException {
        Optional<User> user = getUserById(rs.getInt("viewer_id"));
        Optional<User> friend = getUserById(rs.getInt("friend_id"));

        return new FriendShip(user.get(), friend.get());
    }

    @Getter
    @AllArgsConstructor
    class FriendShip {
        User user;
        User friend;
    }

}
