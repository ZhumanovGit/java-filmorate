package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    final Map<Integer, Film> films;
    int id;
    ValidateService validateService;

    @Autowired
    public InMemoryFilmStorage(ValidateService validateService) {
        this.validateService = validateService;
        this.films = new LinkedHashMap<>();
        this.id = 0;
    }

    int createId() {

        return ++id;
    }
    @Override
    public Film createFilm(Film film) {
        validateService.validateCreateFilm(film);

        film.setId(createId());

        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateService.validateUpdateFilm(film);

        if (films.get(film.getId()) == null) {
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }

        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (films.remove(id) == null) {
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }

    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }

        return film;
    }
}
