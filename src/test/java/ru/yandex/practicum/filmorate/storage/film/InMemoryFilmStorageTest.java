package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryFilmStorageTest {
    InMemoryFilmStorage storage;

    @BeforeEach
    public void beforeEach() {
        storage = new InMemoryFilmStorage();
    }

    void assertEqualsFilm(Film o1, Film o2) {
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getDuration(), o2.getDuration());
        assertEquals(o1.getDescription(), o2.getDescription());
        assertEquals(o1.getReleaseDate(), o2.getReleaseDate());
    }

    @Test
    public void createFilm_shouldCorrectlyCreateFilm_returnFilm() {
        Film film = Film.builder()
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Film newFilm = storage.createFilm(film);

        assertEquals(1, newFilm.getId());
        assertEqualsFilm(film, newFilm);
    }

    @Test
    public void updateFilm_shouldCorrectlyUpdateFilm_returnFilm() {
        Film film = Film.builder()
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

        storage.updateFilm(newFilm);

        assertEquals(1, newFilm.getId());
        assertEquals("updatedFilm", newFilm.getName());
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

        List<Film> films = storage.getFilms();

        assertEquals(3, films.size());
        assertEqualsFilm(films.get(0), firstFilm);
        assertEqualsFilm(films.get(1), secondFilm);
        assertEqualsFilm(films.get(2), thirdFilm);
    }

    @Test
    public void getFilmById_shouldCorrectlyFindFilm_returnFilm() {
        Film firstFilm = Film.builder()
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

        assertEquals(1, storage.getLikes().get(createdFilm.getId()).size());
        assertEquals(Set.of(1), storage.getLikes().get(createdFilm.getId()));
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

        assertEquals(0, storage.getLikes().get(createdFilm.getId()).size());
        assertEquals(new HashSet<>(), storage.getLikes().get(createdFilm.getId()));
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
        storage.getLikes().get(createdFilm.getId()).addAll(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        storage.getLikes().get(secondCreatedFilm.getId()).addAll(Set.of(1, 2, 3, 4, 5));

        List<Film> films = storage.getPopularFilms(2);

        assertEquals(2, films.size());
        assertEqualsFilm(films.get(0), createdFilm);
        assertEqualsFilm(films.get(1), secondCreatedFilm);

    }

}
