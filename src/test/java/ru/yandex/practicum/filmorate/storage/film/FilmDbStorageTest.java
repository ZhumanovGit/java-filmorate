package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void createFilm_whenFilmIsCorrectAndHasGenres_saveAndReturnFilmWithGenres() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film film = Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);
        int createdFilmId = createdFilm.getId();

        assertThat(createdFilm)
                .hasFieldOrPropertyWithValue("id", createdFilmId)
                .hasFieldOrPropertyWithValue("name", "niceName")
                .hasFieldOrPropertyWithValue("description", "its desc")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, Month.MARCH, 25))
                .hasFieldOrPropertyWithValue("duration", 100)
                .hasFieldOrPropertyWithValue("genres", genres)
                .hasFieldOrPropertyWithValue("mpa", mpa);
    }

    @Test
    public void updateFilm_whenFilmIsCorrectAndHasGenres_returnUpdatedFilmWithGenres() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Mpa newMpa = mpaDbStorage.createMpa(new Mpa(2, "PG"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        Genre action = genreDbStorage.createGenre(new Genre(3, "Боевик"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        List<Genre> newGenres = new ArrayList<>();
        genres.add(drama);
        genres.add(action);
        Film oldFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        int oldFilmId = oldFilm.getId();
        Film newFilm = Film.builder()
                .id(oldFilmId)
                .name("newFilm")
                .description("new desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(newGenres)
                .mpa(newMpa)
                .build();

        filmDbStorage.updateFilm(newFilm);

        assertThat(newFilm)
                .hasFieldOrPropertyWithValue("id", oldFilmId)
                .hasFieldOrPropertyWithValue("name", "newFilm")
                .hasFieldOrPropertyWithValue("description", "new desc")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, Month.MARCH, 25))
                .hasFieldOrPropertyWithValue("duration", 100)
                .hasFieldOrPropertyWithValue("genres", newGenres)
                .hasFieldOrPropertyWithValue("mpa", newMpa);
    }

    @Test
    public void deleteFilm_whenCalled_deleteFilmFromBase() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film film = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        int filmId = film.getId();

        filmDbStorage.deleteFilm(filmId);
        Optional<Film> mayBeFilm = filmDbStorage.getFilmById(filmId);

        assertThat(mayBeFilm).isEmpty();
    }

    @Test
    public void deleteAllFilms_whenCalled_deleteAllFilmsFromDatabase() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        Film secondFilm = filmDbStorage.createFilm(Film.builder()
                .name("secondFilm")
                .description("second film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        Film thirdFilm = filmDbStorage.createFilm(Film.builder()
                .name("thirdFilm")
                .description("third film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());

        filmDbStorage.deleteAllFilms();
        List<Film> films = filmDbStorage.getFilms();

        assertThat(films).isEmpty();
    }

    @Test
    public void getFilms_whenStorageHasNoFilms_returnEmptyList() {
        filmDbStorage.deleteAllFilms();

        List<Film> films = filmDbStorage.getFilms();

        assertThat(films).isEmpty();
    }

    @Test
    public void getFilms_whenStorageHasFilms_returnListOfFilms() {

        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        Film secondFilm = filmDbStorage.createFilm(Film.builder()
                .name("secondFilm")
                .description("second film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        Film thirdFilm = filmDbStorage.createFilm(Film.builder()
                .name("thirdFilm")
                .description("third film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());

        List<Film> films = filmDbStorage.getFilms();

        assertThat(films).isNotEmpty();
        assertEquals(films.get(0).getId(), firstFilm.getId());
        assertEquals(films.get(1).getId(), secondFilm.getId());
        assertEquals(films.get(2).getId(), thirdFilm.getId());
    }

    @Test
    public void getFilmById_whenFilmWasFound_returnOptionalWithFilm() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        int firstFilmId = firstFilm.getId();

        Optional<Film> filmOptional = filmDbStorage.getFilmById(firstFilmId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", firstFilmId)
                                .hasFieldOrPropertyWithValue("name", "niceName")
                                .hasFieldOrPropertyWithValue("description", "its desc")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, Month.MARCH, 25))
                                .hasFieldOrPropertyWithValue("duration", 100)
                );
    }

    @Test
    public void getFilmById_thenFilmWasNotFound_returnEmptyOptional() {
        filmDbStorage.deleteAllFilms();

        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void addLike_whenCalled_addLikeToFilm() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        User user = userDbStorage.createUser(User.builder()
                .login("Oleg")
                .email("niceOleg@gg.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Олег")
                .build());

        filmDbStorage.addLike(firstFilm, user);

        assertThat(firstFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    public void deleteLike_whenCalled_deleteLikeFromFilm() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());
        User user = userDbStorage.createUser(User.builder()
                .login("niceOleg")
                .email("niceOleg@g.ru")
                .birthday(LocalDate.of(2020, 12, 1))
                .name("Олег")
                .build());
        filmDbStorage.addLike(firstFilm, user);
        int likesBeforeDelete = firstFilm.getRate();

        filmDbStorage.deleteLike(firstFilm, user);

        assertThat(firstFilm).hasFieldOrPropertyWithValue("rate", 0);
        assertEquals(1, likesBeforeDelete);
    }

    @Test
    public void getPopularFilms_whenCalled_returnListOfFilmsWithBiggestRate() {
        Mpa mpa = mpaDbStorage.createMpa(new Mpa(1, "G"));
        Genre comedy = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre drama = genreDbStorage.createGenre(new Genre(2, "Драма"));
        List<Genre> genres = new ArrayList<>();
        genres.add(comedy);
        genres.add(drama);
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .rate(5)
                .mpa(mpa)
                .build());
        Film secondFilm = filmDbStorage.createFilm(Film.builder()
                .name("secondFilm")
                .description("second film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .rate(4)
                .mpa(mpa)
                .build());
        Film thirdFilm = filmDbStorage.createFilm(Film.builder()
                .name("thirdFilm")
                .description("third film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .rate(3)
                .mpa(mpa)
                .build());

        List<Film> popularFilms = filmDbStorage.getPopularFilms(3);

        assertEquals(popularFilms.get(0).getId(), firstFilm.getId());
        assertEquals(popularFilms.get(1).getId(), secondFilm.getId());
        assertEquals(popularFilms.get(2).getId(), thirdFilm.getId());
    }


}