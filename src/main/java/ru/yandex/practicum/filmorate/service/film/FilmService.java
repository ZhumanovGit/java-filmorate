package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final ValidateService validateService;

    public List<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
    }

    public Film createFilm(Film film) {
        validateService.validateCreateFilm(film);
        return filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        int filmId = film.getId();
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
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        filmStorage.addLike(film, user);
    }

    public void unLikeFilm(int userId, int filmId) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));

        filmStorage.deleteLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
}
