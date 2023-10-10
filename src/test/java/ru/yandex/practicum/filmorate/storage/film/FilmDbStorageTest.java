package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;



@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @BeforeEach
    public void setUp() {
        filmDbStorage.getGenreStorage().createGenre(new Genre(1, "Комедия"));
        filmDbStorage.getGenreStorage().createGenre(new Genre(2, "Драма"));
        filmDbStorage.getGenreStorage().createGenre(new Genre(3, "Мультфильм"));
        filmDbStorage.getGenreStorage().createGenre(new Genre(4, "Триллер"));
        filmDbStorage.getGenreStorage().createGenre(new Genre(5, "Документальный"));
        filmDbStorage.getGenreStorage().createGenre(new Genre(6, "Боевик"));

        filmDbStorage.getMpaStorage().createMpa(new Mpa(1, "G"));
        filmDbStorage.getMpaStorage().createMpa(new Mpa(2, "PG"));
        filmDbStorage.getMpaStorage().createMpa(new Mpa(3, "PG-13"));
        filmDbStorage.getMpaStorage().createMpa(new Mpa(4, "R"));
        filmDbStorage.getMpaStorage().createMpa(new Mpa(5, "NC-17"));
    }

    /*@Test
    public void createFilm_whenFilmIsCorrect_saveAndReturnFilm() {
        Mpa mpa = new Mpa(1, "G");
        Film film = Film.builder()
                .name("niceName")
                .description("ist desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .mpa(mpa)
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);

        assertThat(createdFilm)
                .hasValueSatisfying(film ->
                        assertThat(film).hasFiendOrPropertyWithValue("id", 1));
    }

    @Test
    public void testFindUserById() {

        Optional<Film> userOptional = filmDbStorage.getFilmById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }*/


}