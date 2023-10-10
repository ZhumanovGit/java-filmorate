package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre createGenre(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genre")
                .usingGeneratedKeyColumns("id");
        int genreId = simpleJdbcInsert.executeAndReturnKey(genre.toMap()).intValue();
        genre.setId(genreId);
        return genre;
    }

    @Override
    public void updateGenre(Genre genre) {
        String sqlQuery = "UPDATE genre SET name  = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery, genre.getName(), genre.getId());
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        Genre genre;
        try {
            String sqlQuery = "SELECT * FROM genre WHERE id = ?";
            genre = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeGenre(rs), id);
        } catch (Exception exp) {
            return Optional.empty();
        }
        return Optional.of(genre);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genre";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public void deleteAllGenres() {
        String sqlQuery = "DELETE FROM genre";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void deleteGenreById(int id) {
        String sqlQuery = "DELETE FROM genre WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> getAllGenresForFilm(int filmId) {
        String sqlQuery = "SELECT g.id, g.name FROM film_genre AS fg " +
                "                JOIN genre AS g ON fg.genre_id = g.id " +
                "                WHERE fg.film_id = ?;";
        List<Genre> filmGenres;
        try {
             filmGenres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs), filmId);
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }

        return filmGenres;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
