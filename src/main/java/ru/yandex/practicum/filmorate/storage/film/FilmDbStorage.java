package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

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
                "VALUES (:name, :description, :release_date, :duration_in_minutes, :likes_count, :rating_mpa_id)";

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

        saveGenres(film);

        return film;
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET film_name = :name, description = :description, release_date = :release_date, " +
                "duration_in_minutes = :duration, likes_count = :rate, rating_mpa_id = :mpaId " +
                "WHERE film_id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rate", film.getRate())
                .addValue("mpaId", film.getMpa().getId());

        operations.update(sqlQuery, params);

        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = :id";

        SqlParameterSource filmId = new MapSqlParameterSource("id", film.getId());
        operations.update(sqlQueryForFilmGenres, filmId);

        saveGenres(film);
    }

    @Override
    public void deleteFilm(int id) {
        String sqlQueryForFilmGenres = "DELETE FROM film_genre WHERE film_id = :id";
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
                "FROM films AS f " +
                "JOIN ratingMPA AS r ON f.rating_MPA_id = r.id ";

        List<Film> films = operations.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));

        String sqlQueryForGenres = "SELECT fg.film_id, g.id, g.name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id ";

        List<Genre> genres = operations.query(sqlQueryForGenres, (rs, rowNum) -> makeGenre(rs));

        for (Film film : films) {
            film.setGenres(genres.stream()
                    .filter(genre -> genre.getFilmId() == film.getId())
                    .sorted()
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        return films;
    }

    @Override
    public Optional<Film> getFilmById(int id) {

        String sqlQuery = "SELECT * " +
                "FROM films AS f " +
                "JOIN ratingMPA AS r ON f.rating_MPA_id = r.id " +
                "WHERE film_id = ?;";
        List<Film> films = operations.getJdbcOperations().query(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);

        String sqlQueryForGenres = "SELECT fg.film_id, g.id, g.name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = :filmId;";


        SqlParameterSource needId = new MapSqlParameterSource("filmId", id);

        List<Genre> filmGenres = operations.query(sqlQueryForGenres, needId, (rs, rowNum) -> makeGenre(rs));
        filmGenres.sort(Comparator.comparing(Genre::getId));

        film.setGenres(new LinkedHashSet<>(filmGenres));

        return Optional.of(film);
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQueryForLikes = "INSERT INTO likes(film_id, viewer_id)" +
                "VALUES (?, ?);";
        operations.getJdbcOperations().update(sqlQueryForLikes, film.getId(), user.getId());

        String sqlQuery = "UPDATE films SET likes_count = (SELECT COUNT(viewer_id) FROM likes WHERE film_id = ?)" +
                "WHERE film_id = ?";
        int filmId = film.getId();
        operations.getJdbcOperations().update(sqlQuery, filmId, filmId);

        System.out.println();


    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQueryForLikes = "DELETE FROM likes WHERE film_id = ? AND viewer_id = ?";
        operations.getJdbcOperations().update(sqlQueryForLikes, film.getId(), user.getId());

        String sqlQuery = "UPDATE films SET likes_count = (SELECT COUNT(viewer_id) FROM likes WHERE film_id = ?)" +
                "WHERE film_id = ?";
        int filmId = film.getId();
        operations.getJdbcOperations().update(sqlQuery, filmId, filmId);


    }

    @Override
    public List<Film> getPopularFilms(int count) {

        String sqlQuery = "SELECT f.film_id, " +
                "f.film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration_in_minutes, " +
                "f.likes_count, " +
                "f.rating_MPA_id, " +
                "r.rating_name, " +
                "COUNT(l.viewer_id) AS liked_users " +
                "FROM films AS f " +
                "JOIN ratingMPA AS r ON f.rating_MPA_id = r.id " +
                "LEFT JOIN likes AS l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY liked_users DESC " +
                "LIMIT :limit";
        SqlParameterSource limit = new MapSqlParameterSource("limit", count);
        List<Film> films = operations.query(sqlQuery, limit, (rs, rowNUm) -> makeFilm(rs));

        String sqlQueryForGenres = "SELECT fg.film_id, g.id, g.name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id ";

        List<Genre> genres = operations.query(sqlQueryForGenres, (rs, rowNum) -> makeGenre(rs));

        for (Film film : films) {
            film.setGenres(genres.stream()
                    .filter(genre -> genre.getFilmId() == film.getId())
                    .collect(Collectors.toSet()));
        }

        return films;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(rs.getInt("rating_MPA_id"))
                .name(rs.getString("rating_name")).build();

        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration_in_minutes"))
                .rate(rs.getInt("likes_count"))
                .genres(new LinkedHashSet<>())
                .mpa(mpa)
                .build();

    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .filmId(rs.getInt("film_id"))
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    private void saveGenres(Film film) {

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        Set<Integer> uniqueGenreIds = film.getGenres().stream()
                .map(Genre::getId)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        StringBuilder sb = new StringBuilder("INSERT INTO film_genre(film_id, genre_id) VALUES ");
        MapSqlParameterSource genreParams = new MapSqlParameterSource();
        int counter = 0;
        for (int genreId : uniqueGenreIds) {
            sb.append("(:film_id, :genre_id").append(counter).append("), ");
            genreParams.addValue("film_id", film.getId());
            genreParams.addValue("genre_id" + counter, genreId);
            counter++;
        }
        sb.deleteCharAt(sb.length() - 2);
        operations.update(sb.toString(), genreParams);
    }
}