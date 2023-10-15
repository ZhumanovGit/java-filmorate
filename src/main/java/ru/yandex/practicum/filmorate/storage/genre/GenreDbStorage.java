package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcOperations operations;

    @Override
    public Genre createGenre(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO genre (name) VALUES (:name)";

        SqlParameterSource mpaName = new MapSqlParameterSource("name", genre.getName());

        operations.update(sqlQuery, mpaName, keyHolder);

        int genreId = (int) keyHolder.getKey();
        genre.setId(genreId);
        return genre;
    }

    @Override
    public void updateGenre(Genre genre) {
        String sqlQuery = "UPDATE genre SET name  = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genre.getName())
                .addValue("id", genre.getId());

        operations.update(sqlQuery, params);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {

        String sqlQuery = "SELECT * FROM genre WHERE id = :genreId";
        SqlParameterSource genreId = new MapSqlParameterSource("genreId", id);
        List<Genre> genres = operations.query(sqlQuery, genreId, (rs, rowNum) -> makeGenre(rs));

        if (genres.isEmpty()) {
            return Optional.empty();
        }
        Genre genre = genres.get(0);

        return Optional.of(genre);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genre";

        return operations.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public void deleteAllGenres() {
        String sqlQuery = "DELETE FROM genre";
        operations.getJdbcOperations().update(sqlQuery);
    }

    @Override
    public void deleteGenreById(int id) {
        String sqlQuery = "DELETE FROM genre WHERE id = :id";
        SqlParameterSource genreId = new MapSqlParameterSource("id", id);
        operations.update(sqlQuery, genreId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
