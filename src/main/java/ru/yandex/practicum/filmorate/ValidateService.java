package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class ValidateService {

    public static void validateCreateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.warn("ValidationException, Название пустое");
            throw new ValidateException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("ValidationException, Слишком длинное описание");
            throw new ValidateException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("ValidationException, Некорректная дата выхода");
            throw new ValidateException("Фильм не мог выйти раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("ValidationException, отрицательная продолжительность фильма");
            throw new ValidateException("Продолжительность не может быть отрицательной");
        }
    }

    public static void validateUpdateFilm(Film film) {
        validateCreateFilm(film);
        if (film.getId() == null) {
            log.warn("ValidationException, не передан id фильма");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }

    public static void validateCreateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.warn("ValidationException, Почта пустая");
            throw new ValidateException("Почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("ValidationException, Почта не содержит @");
            throw new ValidateException("Почта должна содержать знак @");
        }

        if (user.getLogin().isBlank()) {
            log.warn("ValidationException, Логин пустой");
            throw new ValidateException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("ValidationException, Лгин содержит пробелы");
            throw new ValidateException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("ValidationException, Некорректная дата");
            throw new ValidateException("Некорректная дата рождения");
        }
    }

    public static void validateUpdateUser(User user) {
        validateCreateUser(user);

        if (user.getId() == null) {
            log.warn("ValidationException, не передан id пользователя");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }
}
