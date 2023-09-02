package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer id = 0;

    private int updateId() {

        return ++id;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);

        film.setId(updateId());

        films.put(film.getId(), film);
        log.info("Фильм с id = {} создан и добавлен в библиотеку", film.getId());
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {

        validateFilm(film);

        if (films.get(film.getId()) == null) {
            log.warn("Исключение ValidateException в PUT запросе, Такого фильма еще не существует в библиотеке");
            throw new ValidateException("Такого фильма еще не существует в библиотеке");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id = {} обновлен", film.getId());
        return film;
    }

    private void validateFilm(Film film) {
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
            log.warn("ValidationException, отрицательная продолжительность фильма {}", film.getDuration());
            throw new ValidateException("Продолжительность не может быть отрицательной");
        }
    }

}
