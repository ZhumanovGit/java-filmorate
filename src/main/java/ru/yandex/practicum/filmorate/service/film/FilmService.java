package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final ValidationService validationService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       ValidationService validationService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validationService = validationService;
    }

    public List<Film> getAll() {
        return filmStorage.getFilms();

    }

    public Film getFilmById(int id) {

        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
    }

    public Film createFilm(Film film) {
        validationService.validateCreateFilm(film);

        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }

        return filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validationService.validateUpdateFilm(film);

        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }

        int filmId = film.getId();
        filmStorage.getFilmById(filmId)
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

        return filmStorage.getPopularFilms(count);
    }
}
