package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage{

    private final JdbcTemplate jdbcTemplate;
    @Override
    public Mpa createMpa(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("ratingMPA")
                .usingGeneratedKeyColumns("id");
        int mpaId = simpleJdbcInsert.executeAndReturnKey(mpa.toMap()).intValue();
        mpa.setId(mpaId);
        return mpa;
    }

    @Override
    public void updateMpa(Mpa mpa) {
        String sqlQuery = "UPDATE ratingMPA SET rating_name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, mpa.getName(), mpa.getId());
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        Mpa mpa;
        try {
            String sqlQuery = "SELECT * FROM ratingMPA WHERE id = ?";
            mpa = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeMpa(rs), id);
        }catch (Exception exp) {
            return Optional.empty();
        }
        return Optional.of(mpa);
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM ratingMPA";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("rating_name"))
                .build();
    }
}
