package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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

    private final JdbcOperations jdbcTemplate;
    @Getter
    private final GenreStorage genreStorage;
    @Getter
    private final MpaStorage mpaStorage;

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        System.out.println(toMap(film));

        int filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();

        film.setId(filmId);

        if (film.getGenres() == null) {
            return film;
        }

        Set<Integer> uniqueGenreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        for (int genreId : uniqueGenreIds) {
            String sqlQueryForGenres = "INSERT INTO film_genre(film_id, genre_id)" +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQueryForGenres, filmId, genreId);
        }

        return film;
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET film_name = ?, description = ?, release_date = ?," +
                "duration_in_minutes = ?, likes_count = ?, rating_mpa_id = ?" +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryForFilmGenres, film.getId());

        if (film.getGenres() == null) {
            return;
        }

        Set<Integer> uniqueGenreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        for (int genreId : uniqueGenreIds) {
            String sqlQueryForGenres = "INSERT INTO film_genre(film_id, genre_id)" +
                    "VALUES (?, ?) ";
            jdbcTemplate.update(sqlQueryForGenres, film.getId(), genreId);
        }
    }

    @Override
    public void deleteFilm(int id) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);

        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryForFilmGenres, id);
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("DELETE FROM films");

        jdbcTemplate.update("DELETE FROM film_genre");
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * " +
                "FROM films;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        Film film;
        try {
            String sqlQuery = "SELECT * " +
                    "FROM films " +
                    "WHERE film_id = ?;";
            film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException exp) {
            return Optional.empty();
        }

        return Optional.of(film);
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQueryForLikes = "INSERT INTO likes(film_id, viewer_id)" +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQueryForLikes, film.getId(), user.getId());
        film.setRate(film.getRate() + 1);
        String sqlQuery = "UPDATE films SET likes_count = ?" +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getRate(), film.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.setRate(film.getRate() - 1);
        String sqlQuery = "UPDATE films SET likes_count = ?" +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getRate(), film.getId());

        String sqlQueryForLikes = "DELETE FROM likes WHERE film_id = ? AND viewer_id = ?";
        jdbcTemplate.update(sqlQueryForLikes, film.getId(), user.getId());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM films " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration_in_minutes"))
                .rate(rs.getInt("likes_count"))
                .build();
        int mpaId = rs.getInt("rating_mpa_id");
        Mpa filmMpa = mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Нет рейтинга с таким id"));
        film.setMpa(filmMpa);

        film.setGenres(genreStorage.getAllGenresForFilm(film.getId()));

        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration_in_minutes", film.getDuration());
        values.put("likes_count", film.getRate());
        values.put("rating_mpa_id", film.getMpa().getId());

        return values;
    }
}
