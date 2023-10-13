package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;

    final GenreStorage genreStorage;
    final MpaStorage mpaStorage;
    final ValidateService validateService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage")FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       ValidateService validateService,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.validateService = validateService;
    }

    public List<Film> getAll() {
        List<Film> films = filmStorage.getFilms();
        for (Film film : films) {
            film.setGenres(genreStorage.getAllGenresForFilm(film.getId()));
            Mpa filmMpa = mpaStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));
            film.setMpa(filmMpa);
        }
        return films;

    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
        film.setGenres(genreStorage.getAllGenresForFilm(film.getId()));
        Mpa filmMpa = mpaStorage.getMpaById(film.getMpa().getId())
                        .orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));
        film.setMpa(filmMpa);
        return film;
    }

    public Film createFilm(Film film) {
        validateService.validateCreateFilm(film);
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        return filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        int filmId = film.getId();
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        Film oldFilm = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));

        filmStorage.updateFilm(film);
    }

    public void deleteAll() {
        filmStorage.deleteAllFilms();
    }

    public void deleteFilmById(int id) {
        filmStorage.deleteFilm(id);
    }

    public void likeFilm(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        filmStorage.addLike(film, user);
    }

    public void unLikeFilm(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        filmStorage.deleteLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        for (Film film : films) {
            film.setGenres(genreStorage.getAllGenresForFilm(film.getId()));
            Mpa filmMpa = mpaStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));
            film.setMpa(filmMpa);
        }
        return films;
    }
}
