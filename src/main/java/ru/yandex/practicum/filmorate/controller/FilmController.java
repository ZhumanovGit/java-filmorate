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
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        Film newFilm = filmService.createFilm(film);
        log.info("Фильм с id = {} создан и добавлен в библиотеку", newFilm);
        return newFilm;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        Film newFilm = filmService.updateFilm(film);
        log.info("Фильм с id = {} обновлен", newFilm.getId());
        return newFilm;
    }

    @DeleteMapping
    public void deleteAllFilms() {
        filmService.deleteAll();
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable int filmId, @PathVariable int userId) {
        filmService.likeFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        filmService.unLikeFilm(userId, filmId);
    }

    @GetMapping("/popular")
    public void getPopularFilms(@RequestParam(required = false) Integer count) {
        filmService.getPopularFilms(count);
    }

}
