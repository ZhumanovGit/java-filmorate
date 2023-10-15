package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Service
@Slf4j
public class ValidationService {

    public void validateCreateFilm(Film film) {
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

    public void validateUpdateFilm(Film film) {
        validateCreateFilm(film);
        if (film.getId() == null) {
            log.warn("ValidationException, не передан id фильма");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }

    public void validateCreateUser(User user) {
        if (user.getEmail() == null) {
            log.warn("ValidationException, Почта null");
            throw new ValidateException("Пользователь не имеет почту");
        }

        if (user.getEmail().isBlank()) {
            log.warn("ValidationException, Почта пустая");
            throw new ValidateException("Почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("ValidationException, Почта не содержит @");
            throw new ValidateException("Почта должна содержать знак @");
        }

        if (user.getLogin() == null) {
            log.warn("ValidationException, Логин null");
            throw new ValidateException("Пользователь не имеет логин");
        }

        if (user.getLogin().isBlank()) {
            log.warn("ValidationException, Логин пустой");
            throw new ValidateException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("ValidationException, Логин содержит пробелы");
            throw new ValidateException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("ValidationException, Некорректная дата");
            throw new ValidateException("Некорректная дата рождения");
        }
    }

    public void validateUpdateUser(User user) {
        validateCreateUser(user);

        if (user.getId() == null) {
            log.warn("ValidationException, не передан id пользователя");
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }
}
