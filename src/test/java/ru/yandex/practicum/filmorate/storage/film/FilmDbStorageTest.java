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
import ru.yandex.practicum.filmorate.model.User;
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
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

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

    @Test
    public void createFilm_whenFilmIsCorrectAndHasGenres_saveAndReturnFilmWithGenres() {
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        System.out.println(mpa);
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        Mpa newMpa = filmDbStorage.getMpaStorage().getMpaById(2).get();;
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
        List<Genre> newGenres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(3).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        assertThat(firstFilm).isIn(films);
        assertThat(secondFilm).isIn(films);
        assertThat(thirdFilm).isIn(films);
    }

    @Test
    public void getFilmById_whenFilmWasFound_returnOptionalWithFilm() {
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
        Film firstFilm = filmDbStorage.createFilm(Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build());

        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "niceName")
                                .hasFieldOrPropertyWithValue("description", "its desc")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, Month.MARCH, 25))
                                .hasFieldOrPropertyWithValue("duration", 100)
                                .hasFieldOrPropertyWithValue("genres", genres)
                                .hasFieldOrPropertyWithValue("mpa", mpa)
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
        Mpa mpa = filmDbStorage.getMpaStorage().getMpaById(1).get();
        List<Genre> genres = new ArrayList<>();
        genres.add(filmDbStorage.getGenreStorage().getGenreById(1).get());
        genres.add(filmDbStorage.getGenreStorage().getGenreById(2).get());
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
                .rate(3)
                .mpa(mpa)
                .build());
        Film thirdFilm = filmDbStorage.createFilm(Film.builder()
                .name("thirdFilm")
                .description("third film desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .rate(1)
                .mpa(mpa)
                .build());

        List<Film> popularFilms = filmDbStorage.getPopularFilms(3);

        assertEquals(popularFilms.get(0), firstFilm);
        assertEquals(popularFilms.get(1), secondFilm);
        assertEquals(popularFilms.get(2), thirdFilm);
    }


}