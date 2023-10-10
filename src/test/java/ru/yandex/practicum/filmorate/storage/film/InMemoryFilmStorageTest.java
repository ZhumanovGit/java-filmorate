package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryFilmStorageTest {
    InMemoryFilmStorage storage;

    @BeforeEach
    public void beforeEach() {
        storage = new InMemoryFilmStorage();
    }

    void assertEqualsFilm(Film o1, Film o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getDuration(), o2.getDuration());
        assertEquals(o1.getDescription(), o2.getDescription());
        assertEquals(o1.getReleaseDate(), o2.getReleaseDate());
    }

    @Test
    public void createFilm_shouldCorrectlyCreateFilm_returnFilm() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        List<Film> expectedFilmsList = new ArrayList<>();
        expectedFilmsList.add(film);

        Film newFilm = storage.createFilm(film);

        assertEqualsFilm(film, newFilm);
        assertEquals(expectedFilmsList, storage.getFilms());
        assertEquals(0, newFilm.getRate());
    }

    @Test
    public void updateFilm_shouldCorrectlyUpdateFilm_returnFilm() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film newFilm = Film.builder()
                .id(1)
                .name("updatedFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        storage.createFilm(film);
        List<Film> expectedFilmsList = new ArrayList<>();
        expectedFilmsList.add(newFilm);

        storage.updateFilm(newFilm);

        assertEquals(film.getId(), newFilm.getId());
        assertEquals("updatedFilm", newFilm.getName());
        assertEquals(expectedFilmsList, storage.getFilms());
    }

    @Test
    public void deleteAllFilms_shouldDeleteAllFilms_void() {
        Film firstFilm = Film.builder()
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film secondFilm = Film.builder()
                .name("secondFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2001, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film thirdFilm = Film.builder()
                .name("thirdFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2002, Month.JANUARY, 1))
                .duration(120)
                .build();
        storage.createFilm(firstFilm);
        storage.createFilm(secondFilm);
        storage.createFilm(thirdFilm);

        storage.deleteAllFilms();

        assertEquals(0, storage.getFilms().size());
    }

    @Test
    public void getFilms_shouldCorrectlyReturnAllFilms_ListOfFilms() {
        Film firstFilm = Film.builder()
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film secondFilm = Film.builder()
                .name("secondFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2001, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film thirdFilm = Film.builder()
                .name("thirdFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2002, Month.JANUARY, 1))
                .duration(120)
                .build();
        storage.createFilm(firstFilm);
        storage.createFilm(secondFilm);
        storage.createFilm(thirdFilm);
        List<Film> expectedFilmsList = new ArrayList<>();
        expectedFilmsList.add(firstFilm);
        expectedFilmsList.add(secondFilm);
        expectedFilmsList.add(thirdFilm);


        List<Film> films = storage.getFilms();

        assertEquals(3, films.size());
        assertEqualsFilm(films.get(0), firstFilm);
        assertEqualsFilm(films.get(1), secondFilm);
        assertEqualsFilm(films.get(2), thirdFilm);
        assertEquals(expectedFilmsList, storage.getFilms());
    }

    @Test
    public void getFilmById_shouldCorrectlyFindFilm_returnFilm() {
        Film firstFilm = Film.builder()
                .id(1)
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film createdFilm = storage.createFilm(firstFilm);

        Optional<Film> ourFilm = storage.getFilmById(createdFilm.getId());

        assertEqualsFilm(createdFilm, ourFilm.get());
    }

    @Test
    public void getFilmById_whenFilmIsNull_returnEmptyOptional() {

        Optional<Film> ourFilm = storage.getFilmById(5);

        assertEquals(true, ourFilm.isEmpty());
    }

    @Test
    public void addLike_shouldCorrectlyAddLike_void() {
        Film firstFilm = Film.builder()
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        User user = User.builder()
                .id(1)
                .login("asdasd")
                .email("asdasd@asd.ru")
                .birthday(LocalDate.of(2003, 1, 1))
                .build();
        Film createdFilm = storage.createFilm(firstFilm);

        storage.addLike(createdFilm, user);

        assertEquals(1, createdFilm.getRate());
    }

    @Test
    public void deleteLike_shouldCorrectlyDeleteLike_void() {
        Film firstFilm = Film.builder()
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        User user = User.builder()
                .id(1)
                .login("asdasd")
                .email("asdasd@asd.ru")
                .birthday(LocalDate.of(2003, 1, 1))
                .build();
        Film createdFilm = storage.createFilm(firstFilm);
        storage.addLike(createdFilm, user);

        storage.deleteLike(createdFilm, user);

        assertEquals(0, createdFilm.getRate());
    }

    @Test
    public void getPopularFilms_shouldCorrectlyReturnPopularFilms_ListOfFilms() {
        Film firstFilm = Film.builder()
                .name("firstFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film secondFilm = Film.builder()
                .name("secondFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        Film createdFilm = storage.createFilm(firstFilm);
        Film secondCreatedFilm = storage.createFilm(secondFilm);
        createdFilm.setRate(10);
        secondCreatedFilm.setRate(5);

        List<Film> films = storage.getPopularFilms(2);

        assertEquals(2, films.size());
        assertEqualsFilm(films.get(0), createdFilm);
        assertEqualsFilm(films.get(1), secondCreatedFilm);

    }

}
