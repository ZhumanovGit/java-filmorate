package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final NamedParameterJdbcOperations operations;

    @Override
    public Mpa createMpa(Mpa mpa) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO ratingMPA (rating_name) VALUES (:name)";

        SqlParameterSource mpaName = new MapSqlParameterSource("name", mpa.getName());

        operations.update(sqlQuery, mpaName, keyHolder);

        int mpaId = (int) keyHolder.getKey();
        mpa.setId(mpaId);
        return mpa;
    }

    @Override
    public void updateMpa(Mpa mpa) {
        String sqlQuery = "UPDATE ratingMPA SET rating_name = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", mpa.getName())
                .addValue("id", mpa.getId());
        operations.update(sqlQuery, params);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {

        String sqlQuery = "SELECT * FROM ratingMPA WHERE id = :mpaId";
        SqlParameterSource mpaId = new MapSqlParameterSource("mpaId", id);
        List<Mpa> mpas = operations.query(sqlQuery, mpaId, (rs, rowNum) -> makeMpa(rs));

        if (mpas.isEmpty()) {
            return Optional.empty();
        }
        Mpa mpa = mpas.get(0);

        return Optional.of(mpa);
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM ratingMPA";
        return operations.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public void deleteAllMpa() {
        String sqlQuery = "DELETE FROM ratingMPA";
        operations.getJdbcOperations().update(sqlQuery);
    }

    @Override
    public void deleteMpaById(int id) {
        String sqlQuery = "DELETE FROM ratingMPA WHERE id = :id";
        SqlParameterSource mpaId = new MapSqlParameterSource("id", id);
        operations.update(sqlQuery, mpaId);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("rating_name"))
                .build();
    }
}
