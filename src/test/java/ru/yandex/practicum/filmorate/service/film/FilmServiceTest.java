package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FilmServiceTest {
    FilmStorage storage;
    UserStorage userStorage;
    FilmService filmService;
    GenreStorage genreStorage;
    MpaStorage mpaStorage;

    @BeforeEach
    public void beforeEach() {
        storage = mock(FilmStorage.class);
        userStorage = mock(UserStorage.class);
        genreStorage = mock(GenreStorage.class);
        mpaStorage = mock(MpaStorage.class);
        filmService = new FilmService(storage, userStorage, new ValidateService(), genreStorage, mpaStorage);
    }

    void assertEqualsFilm(Film o1, Film o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getDuration(), o2.getDuration());
        assertEquals(o1.getDescription(), o2.getDescription());
        assertEquals(o1.getReleaseDate(), o2.getReleaseDate());
    }

    @Test
    public void getFilms_whenCalled_thenReturnedListOfFilms() {
        Mpa mpa = new Mpa(1, "test");
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        Film secondFilm = Film.builder()
                .id(2)
                .name("secondFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2001, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        Film thirdFilm = Film.builder()
                .id(3)
                .name("thirdFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2002, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        when(mpaStorage.getMpaById(film.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(mpaStorage.getMpaById(secondFilm.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(mpaStorage.getMpaById(thirdFilm.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(storage.getFilms()).thenReturn(List.of(film, secondFilm, thirdFilm));
        List<Film> expectedFilms = List.of(film, secondFilm, thirdFilm);

        List<Film> actualFilms = filmService.getAll();

        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    public void getFilms_whenZeroFilms_thenReturnedEmptyList() {
        when(storage.getFilms()).thenReturn(new ArrayList<>());
        List<Film> expectedFilms = new ArrayList<>();

        List<Film> actualFilms = filmService.getAll();

        assertEquals(expectedFilms, actualFilms);
        assertEquals(0, actualFilms.size());
    }

    @Test
    public void getFilmById_whenFilmWasFound_thenReturnedFilm() {
        Mpa mpa = new Mpa(1, "test");
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        when(mpaStorage.getMpaById(film.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(storage.getFilmById(film.getId())).thenReturn(Optional.of(film));

        Film actualFilm = filmService.getFilmById(1);

        assertEqualsFilm(film, actualFilm);
    }

    @Test
    public void getFilmById_whenFilmWasNotFound_thenThrowException() {
        when(storage.getFilmById(1)).thenReturn(Optional.empty());
        String expectedResponse = "Такого фильма еще не существует в библиотеке";

        Throwable throwable = assertThrows(NotFoundException.class, () -> filmService.getFilmById(1));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createFilm_whenValidationWasSuccess_thenReturnedFilm() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(new Mpa(1, "test"))
                .build();
        when(storage.createFilm(film)).thenReturn(film);

        Film actualFilm = filmService.createFilm(film);

        assertEqualsFilm(film, actualFilm);
    }

    @Test
    public void createFilm_whenValidationWasNotSuccess_thenThrowException() {
        Film film = Film.builder()
                .id(1)
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();
        String expectedResponse = "Фильм не имеет названия";

        Throwable throwable = assertThrows(ValidateException.class, () -> filmService.createFilm(film));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void likeFilm_whenUserAndFilmIsCorrect_thenAdd1toLikesCount() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(new Mpa(1, "test"))
                .build();
        User user = User.builder()
                .id(1)
                .login("login")
                .email("email@ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        when(storage.createFilm(film)).thenReturn(film);
        filmService.createFilm(film);
        when(storage.getFilmById(film.getId())).thenReturn(Optional.of(film));
        when(userStorage.getUserById(user.getId())).thenReturn(Optional.of(user));

        filmService.likeFilm(user.getId(), film.getId());

        verify(storage).addLike(film, user);
    }

    @Test
    public void unlikeFilm_whenUserAndFilmIsCorrect_thenRemove1fromLikesCount() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(new Mpa(1, "test"))
                .build();
        User user = User.builder()
                .id(1)
                .login("login")
                .email("email@ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        when(storage.createFilm(film)).thenReturn(film);
        filmService.createFilm(film);
        when(storage.getFilmById(1)).thenReturn(Optional.of(film));
        when(userStorage.getUserById(1)).thenReturn(Optional.of(user));
        filmService.likeFilm(user.getId(), film.getId());

        filmService.unLikeFilm(user.getId(), film.getId());

        verify(storage).deleteLike(film, user);
    }

    @Test
    public void getPopularFilms_whenFilmsIsEmpty_thenReturnedEmptyList() {
        when(storage.getPopularFilms(5)).thenReturn(new ArrayList<>());
        List<Film> expectedList = new ArrayList<>();

        List<Film> actualList = filmService.getPopularFilms(5);

        assertEquals(expectedList, actualList);
    }

    @Test
    public void getPopularFilms_whenFilmsIsNotEmpty_thenReturnedEmptyList() {
        Mpa mpa = new Mpa(1, "test");
        Genre genre = new Genre(1, "testGenre");
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        Film secondFilm = Film.builder()
                .id(2)
                .name("secondFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2001, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        Film thirdFilm = Film.builder()
                .id(3)
                .name("thirdFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2002, Month.JANUARY, 1))
                .duration(120)
                .mpa(mpa)
                .build();
        when(genreStorage.getAllGenresForFilm(film.getId())).thenReturn(List.of(genre));
        when(genreStorage.getAllGenresForFilm(secondFilm.getId())).thenReturn(List.of(genre));
        when(genreStorage.getAllGenresForFilm(thirdFilm.getId())).thenReturn(List.of(genre));
        when(mpaStorage.getMpaById(film.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(mpaStorage.getMpaById(secondFilm.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(mpaStorage.getMpaById(thirdFilm.getMpa().getId())).thenReturn(Optional.of(mpa));
        when(storage.getPopularFilms(3)).thenReturn(List.of(film, secondFilm, thirdFilm));
        List<Film> expectedList = new ArrayList<>(List.of(film, secondFilm, thirdFilm));

        List<Film> actualList = filmService.getPopularFilms(3);

        assertEquals(expectedList, actualList);
    }

}