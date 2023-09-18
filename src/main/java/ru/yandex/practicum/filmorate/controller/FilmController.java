package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.WrongArgumentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

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
        List<Film> films = filmService.getAll();
        log.info("Получен список фильмов");
        return films;
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.info("Обработка запроса с получением фильма с id = {}", filmId);
        Film film = filmService.getFilmById(filmId);
        log.info("Получен фильм с id = {}", filmId);
        return film;
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        log.info("Обработка запроса с созданием фильма");
        Film newFilm = filmService.createFilm(film);
        int newFilmId = newFilm.getId();
        log.info("Создан фильм с id = {}", newFilmId);
        return newFilm;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        int filmId = film.getId();
        log.info("Обработка запроса с обновлением фильма с id = {}", filmId);
        filmService.updateFilm(film);
        log.info("Обновлен фильм с id = {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        log.info("Обработка запроса с удалением всех фильмов");
        filmService.deleteAll();
        log.info("Все фильмы удалены");
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        log.info("Обработка запроса с удалением фильма с id = {}", filmId);
        filmService.deleteFilmById(filmId);
        log.info("Фильм с id = {} удален", filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeFilm(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Обработка запроса с добавлением лайка фильму с id = {} пользователем с id = {}", filmId, userId);
        filmService.likeFilm(userId, filmId);
        log.info("Фильм с id = {} оценен пользоветлем с id = {}", filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        log.info("Обработка запроса с удалением лайка фильму с id = {} пользователем с id = {}", filmId, userId);
        filmService.unLikeFilm(userId, filmId);
        log.info("Фильм с id = {}, убран лайк от пользователя  с id = {}", filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Обработка запроса с получением списка популярных фильмов");
        if (count <= 0) {
            throw new WrongArgumentException("Недопустимое значение count");
        }
        List<Film> films = filmService.getPopularFilms(count);
        log.info("Получен список поплуярных фильмов длиной {}", count);
        return films;
    }

}
