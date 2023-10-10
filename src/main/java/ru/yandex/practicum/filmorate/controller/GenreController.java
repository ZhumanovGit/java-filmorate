package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Обработка запроса с получением всех жанров");
        List<Genre> genres = genreService.getAll();
        log.info("Получен список всех жанров");
        return genres;
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable int genreId) {
        log.info("Обработка запроса с получением жанра с id = {}", genreId);
        Genre genre = genreService.getGenreById(genreId);
        log.info("Получен жанр с id = {}", genreId);
        return genre;
    }

    @PostMapping
    public Genre createGenre(@RequestBody Genre genre) {
        log.info("Обработка запроса с созданием нового жанра");
        Genre newGenre = genreService.createGenre(genre);
        int newGenreId = newGenre.getId();
        log.info("Создан жанр с id = {}", newGenreId);
        return newGenre;
    }

    @PutMapping
    public Genre updateGenre(@RequestBody Genre genre) {
        int genreId = genre.getId();
        log.info("Обрботка запроса с обновлением жанра с id = {}", genreId);
        genreService.updateGenre(genre);
        log.info("Обновлен жанр с id = {}", genreId);
        return genreService.getGenreById(genreId);
    }

    @DeleteMapping("/{genreId}")
    public void deleteGenreById(@PathVariable int genreId) {
        log.info("Обработка запроса с удалением жанра с id = {}", genreId);
        genreService.deleteGenreById(genreId);
        log.info("Жанр с id = {} удален", genreId);
    }

    @DeleteMapping
    public void deleteAllGenres() {
        log.info("Обработка запроса с удалением всех жанров");
        genreService.deleteAllGenres();
        log.info("Все жанры удалены");
    }
}
