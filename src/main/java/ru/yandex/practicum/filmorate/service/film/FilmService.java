package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAll() {
        return filmStorage.getFilms();

    }

    public Film getFilmById(int id) {

        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Такого фильма еще не существует в библиотеке"));
    }

    public Film createFilm(Film film) {
        validateCreateFilm(film);
        return filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validateUpdateFilm(film);
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

    private void validateCreateFilm(Film film) {
        if (film.getName() == null) {
            log.warn("ValidationException, Название null");
            throw new ValidateException("Фильм не имеет названия");
        }

        if (film.getName().isBlank()) {
            log.warn("ValidationException, Название пустое");
            throw new ValidateException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("ValidationException, Слишком длинное описание");
            throw new ValidateException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.warn("ValidationException, Нет даты выхода");
            throw new ValidateException("Фильм не может не иметь даты выхода");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("ValidationException, Некорректная дата выхода");
            throw new ValidateException("Фильм не мог выйти раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("ValidationException, отрицательная продолжительность фильма");
            throw new ValidateException("Продолжительность не может быть отрицательной");
        }

        if (film.getMpa() == null) {
            log.warn("ValidationException, фильм не имеет возрастного рейтинга");
            throw new ValidateException("Возраcтной рейтинг не указан");
        }
    }

    private void validateUpdateFilm(Film film) {
        validateCreateFilm(film);
        if (film.getId() == null) {
            log.warn("ValidationException, не передан id фильма");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }
}
