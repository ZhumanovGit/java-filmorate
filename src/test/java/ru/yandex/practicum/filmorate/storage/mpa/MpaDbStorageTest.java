package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(MpaDbStorage.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    public void createMpa_whenMpaIsCorrect_thanReturnMpa() {
        Mpa mpa = new Mpa(1, "G");

        Mpa newMpa = mpaDbStorage.createMpa(mpa);
        int newMpaId = newMpa.getId();

        assertThat(newMpa).hasFieldOrPropertyWithValue("id", newMpaId)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void createMpa_whenMpaHasNoId_thanReturnMpaWithId() {
        Mpa mpa = Mpa.builder().name("G").build();

        Mpa newMpa = mpaDbStorage.createMpa(mpa);
        int newMpaId = newMpa.getId();

        assertThat(newMpa).hasFieldOrPropertyWithValue("id", newMpaId)
                .hasFieldOrPropertyWithValue("name", "G");

    }

    @Test
    public void updateMpa_whenCalled_thanUpdateMpa() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        int mpaId = mpa.getId();
        Mpa newMpa = Mpa.builder()
                .id(mpaId)
                .name("newMpa")
                .build();

        mpaDbStorage.updateMpa(newMpa);

        assertThat(newMpa).hasFieldOrPropertyWithValue("id", mpaId)
                .hasFieldOrPropertyWithValue("name", "newMpa");
    }

    @Test
    public void getMpaById_whenMpaWasFound_returnOptionalWithMpa() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        int mpaId = mpa.getId();

        Optional<Mpa> optionalMpa = mpaDbStorage.getMpaById(mpaId);

        assertThat(optionalMpa)
                .isPresent().hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("id", mpaId)
                        .hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void getMpaById_whenMpaIsNotFound_thanReturnEmptyOptional() {
        mpaDbStorage.deleteAllMpa();

        Optional<Mpa> optionalMpa = mpaDbStorage.getMpaById(1);

        assertThat(optionalMpa).isEmpty();
    }

    @Test
    public void getAll_whenStorageHasMpa_thanReturnListOfMpa() {
        mpaDbStorage.deleteAllMpa();
        Mpa firstMpa = mpaDbStorage.createMpa(Mpa.builder().name("G").build());
        Mpa secondMpa = mpaDbStorage.createMpa(Mpa.builder().name("PG").build());
        Mpa thirdMpa = mpaDbStorage.createMpa(Mpa.builder().name("PG-13").build());

        List<Mpa> mpas = mpaDbStorage.getAll();

        assertThat(mpas).isNotEmpty();
        assertEquals(mpas.get(0).getId(), firstMpa.getId());
        assertEquals(mpas.get(1).getId(), secondMpa.getId());
        assertEquals(mpas.get(2).getId(), thirdMpa.getId());
    }

    @Test
    public void getAll_whenStorageHasNoMpa_thanReturnEmptyList() {
        mpaDbStorage.deleteAllMpa();

        List<Mpa> mpas = mpaDbStorage.getAll();

        assertThat(mpas).isEmpty();
    }

    @Test
    public void deleteAllMpas_whenCalled_thanDeleteAllMpas() {
        Mpa firstMpa = mpaDbStorage.createMpa(Mpa.builder().name("G").build());
        Mpa secondMpa = mpaDbStorage.createMpa(Mpa.builder().name("PG").build());
        Mpa thirdMpa = mpaDbStorage.createMpa(Mpa.builder().name("PG-13").build());

        mpaDbStorage.deleteAllMpa();
        List<Mpa> mpas = mpaDbStorage.getAll();

        assertThat(mpas).isEmpty();
    }

    @Test
    public void deleteMpaById_whenCalled_thanDeleteNeedMpa() {
        Mpa firstMpa = mpaDbStorage.createMpa(Mpa.builder().name("G").build());
        int mpaId = firstMpa.getId();

        mpaDbStorage.deleteMpaById(mpaId);
        List<Mpa> mpas = mpaDbStorage.getAll();

        assertThat(firstMpa).isNotIn(mpas);
    }
}