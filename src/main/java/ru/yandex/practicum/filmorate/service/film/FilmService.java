package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

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
        Optional<Film> mayBeFilm = filmStorage.getFilmById(id);
        if (mayBeFilm.isEmpty()) {
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }
        return mayBeFilm.get();
    }

    public Film createFilm(Film film) {
        validateService.validateCreateFilm(film);
        return filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        int filmId = film.getId();
        Film oldFilm = getFilmById(filmId);
        film.setLikesCount(oldFilm.getLikesCount());

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
        Optional<User> mayBeUser = userStorage.getUserById(userId);

        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        int newLikesCount = film.getLikesCount() + 1;
        film.setLikesCount(newLikesCount);

        filmStorage.addLike(film, mayBeUser.get());
    }

    public void unLikeFilm(int userId, int filmId) {
        Film film = getFilmById(filmId);
        Optional<User> mayBeUser = userStorage.getUserById(userId);

        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        int newLikesCount = film.getLikesCount() - 1;
        film.setLikesCount(newLikesCount);

        filmStorage.deleteLike(film, mayBeUser.get());
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
}
