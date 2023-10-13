package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcOperations operations;

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO films " +
                "(film_name, description, release_date, duration_in_minutes, likes_count, rating_mpa_id) " +
                "VALUE (:name, :description, :release_date, :duration_in_minutes, :likes_count, :rating_mpa_id)";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration_in_minutes", film.getDuration())
                .addValue("likes_count", film.getRate())
                .addValue("rating_mpa_id", film.getMpa().getId());

        operations.update(sqlQuery, params, keyHolder);

        int filmId = (int) keyHolder.getKey();
        film.setId(filmId);

        if (film.getGenres().isEmpty()) {
            return film;
        }

        saveGenres(film);

        return film;
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET film_name = :name, description = :description, release_date = :release_date," +
                "duration_in_minutes = :duration, likes_count = :rate, rating_mpa_id = mpa_id" +
                "WHERE film_id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration_in_minutes", film.getDuration())
                .addValue("likes_count", film.getRate())
                .addValue("rating_mpa_id", film.getMpa().getId());

        operations.update(sqlQuery, params);

        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = :id";

        SqlParameterSource filmId = new MapSqlParameterSource("id", film.getId());
        operations.update(sqlQueryForFilmGenres, filmId);

        if (film.getGenres().isEmpty()) {
            return;
        }

        saveGenres(film);
    }

    @Override
    public void deleteFilm(int id) {
        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = ?";
        SqlParameterSource filmId = new MapSqlParameterSource("id", id);
        operations.update(sqlQueryForFilmGenres, filmId);

        String sqlQuery = "DELETE FROM films WHERE film_id = :id";
        operations.update(sqlQuery, filmId);

    }

    @Override
    public void deleteAllFilms() {
        operations.getJdbcOperations().update("DELETE FROM film_genre");

        operations.getJdbcOperations().update("DELETE FROM films");

    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * " +
                "FROM films;";

        return operations.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        Film film;
        try {
            String sqlQuery = "SELECT * " +
                    "FROM films " +
                    "WHERE film_id = :id;";
            SqlParameterSource filmId = new MapSqlParameterSource("id", id);
            film = operations.query(sqlQuery, filmId, this::makeFilm);
        } catch (DataAccessException exp) {
            return Optional.empty();
        }

        return Optional.of(film);
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQueryForLikes = "INSERT INTO likes(film_id, viewer_id)" +
                "VALUES (:filmId, :userId);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", film.getId())
                .addValue("userId", user.getId());
        operations.update(sqlQueryForLikes,params);

        film.setRate(film.getRate() + 1);
        String sqlQuery = "UPDATE films SET likes_count = (SELECT COUNT (viewer_id) FROM likes WHERE film_id = :filmId) " +
                "WHERE film_id = :filmId";
        SqlParameterSource filmId = new MapSqlParameterSource("filmId", film.getId());
        operations.update(sqlQuery, filmId);

    }

    @Override
    public void deleteLike(Film film, User user) {

        String sqlQueryForLikes = "DELETE FROM likes WHERE film_id = :filmId, viewer_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", film.getId())
                .addValue("userId", user.getId());
        operations.update(sqlQueryForLikes,params);

        film.setRate(film.getRate() - 1);
        String sqlQuery = "UPDATE films SET likes_count = (SELECT COUNT (viewer_id) FROM likes WHERE film_id = :filmId) " +
                "WHERE film_id = :filmId";
        SqlParameterSource filmId = new MapSqlParameterSource("filmId", film.getId());
        operations.update(sqlQuery, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM films " +
                "ORDER BY likes_count DESC " +
                "LIMIT :limit";
        SqlParameterSource limit = new MapSqlParameterSource("limit", count);
        return operations.query(sqlQuery, limit, (rs, rowNUm) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration_in_minutes"))
                .rate(rs.getInt("likes_count"))
                .mpa(Mpa.builder().id(rs.getInt("rating_mpa_id")).build())
                .build();
    }

    private void saveGenres(Film film) {

        Set<Integer> uniqueGenreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder("INSERT INTO film_genre(film_id, genre_id) ");
        MapSqlParameterSource genreParams = new MapSqlParameterSource();
        genreParams.addValue("film_id", film.getId());
        int counter = 0;
        for (int genreId : uniqueGenreIds) {
            sb.append("VALUES (:film_id, :genre_id").append(counter).append("),");
            genreParams.addValue("genre_id" + counter, genreId);
            counter++;
        }
        sb.deleteCharAt(sb.length() - 1);
        operations.update(sb.toString(), genreParams);
    }
}