package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Service
public class ValidationService {

    public void validateCreateFilm(Film film) {
        if (film.getName() == null) {
            throw new ValidateException("Фильм не имеет названия");
        }

        if (film.getName().isBlank()) {
            throw new ValidateException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidateException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidateException("Фильм не может не иметь даты выхода");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidateException("Фильм не мог выйти раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidateException("Продолжительность не может быть отрицательной");
        }

        if (film.getMpa() == null) {
            throw new ValidateException("Возраcтной рейтинг не указан");
        }
    }

    public void validateUpdateFilm(Film film) {
        validateCreateFilm(film);
        if (film.getId() == null) {
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }

    public void validateCreateUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidateException("Пользователь не имеет почту");
        }

        if (user.getEmail().isBlank()) {
            throw new ValidateException("Почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidateException("Почта должна содержать знак @");
        }

        if (user.getLogin() == null) {
            throw new ValidateException("Пользователь не имеет логин");
        }

        if (user.getLogin().isBlank()) {
            throw new ValidateException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidateException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("Некорректная дата рождения");
        }
    }

    public void validateUpdateUser(User user) {
        validateCreateUser(user);

        if (user.getId() == null) {
            throw new ValidateException("Не найден id для PUT запроса");
        }
    }
}
