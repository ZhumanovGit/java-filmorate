package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;


import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Обработка запроса с получением списка фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.info("Обработка запроса с получением фильма с id = {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        log.info("Обработка запроса с созданием фильма");
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        log.info("Обработка запроса с обновлением фильма с id = {}", film.getId());
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        log.info("Обработка запроса с удалением всех фильмов");
        filmService.deleteAll();
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.info("Обработка запроса с удалением фильма с id = {}", filmId);
        filmService.deleteFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Обработка запроса с добавлением лайка фильму с id = {} пользователем с id = {}", filmId, userId);
        filmService.likeFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        log.info("Обработка запроса с удалением лайка фильму с id = {} пользователем с id = {}", filmId, userId);
        filmService.unLikeFilm(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Обработка запроса с получением списка популярных фильмов");
        return filmService.getPopularFilms(count);
    }

}
