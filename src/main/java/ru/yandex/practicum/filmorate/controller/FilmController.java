package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    private int id = 0;

    private int createId() {

        return ++id;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        ValidateService.validateCreateFilm(film);

        film.setId(createId());

        films.put(film.getId(), film);
        log.info("Фильм с id = {} создан и добавлен в библиотеку", film.getId());
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {

        ValidateService.validateUpdateFilm(film);

        if (films.get(film.getId()) == null) {
            log.warn("Исключение NotFoundException в PUT запросе, Такого фильма еще не существует в библиотеке");
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id = {} обновлен", film.getId());
        return film;
    }

}
