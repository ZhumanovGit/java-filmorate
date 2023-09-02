package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
    }

    @Test
    public void shouldReturnEmptyListOfFilms() {
        List<Film> films = controller.getFilms();

        assertEquals(new ArrayList<Film>(), films);
    }

    @Test
    public void shouldReturnListOfUsers() {
        Film firstFilm = controller.createFilm(Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build());
        Film secondFilm = controller.createFilm(Film.builder()
                .id(1)
                .name("secondTestFilm")
                .description("test desc for second film")
                .releaseDate(LocalDate.of(2002, Month.JANUARY, 1))
                .duration(120)
                .build());

        List<Film> films = controller.getFilms();

        Film ourFirstFilm = films.get(0);
        Film ourSecondFilm = films.get(1);

        assertEquals(2, films.size());
        assertEquals(firstFilm, ourFirstFilm);
        assertEquals(secondFilm, ourSecondFilm);
    }

    @Test
    public void shouldCorrectlyCreateFilm() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Film ourFilm = controller.createFilm(Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build());

        assertEquals(film, ourFilm);
    }

    @Test
    public void shouldThrowExceptionIfFilmHasNoName() {
        assertThrows(NullPointerException.class, () -> Film.builder()
                .id(1)
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build());

    }

    @Test
    public void shouldThrowExceptionIfFilmHasBadDuration() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.createFilm(Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(0)
                .build()));

        assertEquals("Продолжительность не может быть отрицательной", throwable.getMessage());

    }

    @Test
    public void shouldThrowExceptionIfFilmHasNoReleaseDate() {
        assertThrows(NullPointerException.class, () -> Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .duration(120)
                .build());

    }

    @Test
    public void shouldThrowExceptionIfFilmHasBlankName() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.createFilm(Film.builder()
                .id(1)
                .name("   ")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build()));

        assertEquals("Название не может быть пустым", throwable.getMessage());

    }

    @Test
    public void shouldThrowExceptionIfDescriptionIsLongerThan200Chars() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.createFilm(Film.builder()
                .id(1)
                .name("testName")
                .description("very long test desc for film very long test desc for film very long test desc for " +
                        "film very long test desc for film very long test desc for film very long test desc for film " +
                        "very long test desc for film very long test desc for film very long test desc for film very " +
                        "long test desc for film very long test desc for film very long test desc for film very long " +
                        "test desc for film very long test desc for film very long test desc for film very long test " +
                        "desc for film very long test desc for film ")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build()));

        assertEquals("Описание не может быть длиннее 200 символов", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfFilmReleaseDateIsBeforeThan28DecemberOf1895() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.createFilm(Film.builder()
                .id(1)
                .name("testName")
                .description("test desc for film")
                .releaseDate(LocalDate.of(1700, Month.JANUARY, 1))
                .duration(120)
                .build()));

        assertEquals("Фильм не мог выйти раньше 28 декабря 1895 года", throwable.getMessage());

    }

    @Test
    public void shouldCorrectlyUpdateFilm() {
        Film film = controller.createFilm(Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build());

        Film newFilm = controller.updateFilm(Film.builder()
                .id(film.getId())
                .name("newTestFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build());

        Film result = controller.getFilms().get(0);

        assertEquals(result, newFilm);
        assertEquals(film.getId(), newFilm.getId());
    }

    @Test
    public void shouldThrowExceptionIfIdOfFilmIsNotFound() {
        Throwable throwable = assertThrows(ValidateException.class, () -> controller.updateFilm(Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build()));
        assertEquals("Такого фильма еще не существует в библиотеке", throwable.getMessage());
    }

}