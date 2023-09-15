package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.WrongArgumentException;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    final Map<Integer, Film> films;
    int id;
    ValidateService validateService;
    Map<Integer, Set<Integer>> likes;

    @Autowired
    public InMemoryFilmStorage(ValidateService validateService) {
        this.validateService = validateService;
        this.films = new LinkedHashMap<>();
        this.id = 0;
        this.likes = new HashMap<>();
    }

    @Override
    public Map<Integer, Set<Integer>> getLikes() {
        return likes;
    }

    int createId() {

        return ++id;
    }

    @Override
    public Film createFilm(Film film) {
        validateService.validateCreateFilm(film);

        film.setId(createId());

        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateService.validateUpdateFilm(film);

        if (films.get(film.getId()) == null) {
            throw new NotFoundException("Такого фильма еще не существует в библиотеке");
        }

        films.put(film.getId(), film);
        if (likes.get(film.getId()) == null) {
            likes.put(film.getId(), new HashSet<>());
        }
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

    @Override
    public void addLike(int id, int userId) {
        Set<Integer> newLikesOfFilm = likes.get(id);
        newLikesOfFilm.add(userId);
        likes.put(id, newLikesOfFilm);
    }

    @Override
    public void deleteLike(int id, int userId) {
        Set<Integer> newLikesOfFilm = likes.get(id);
        newLikesOfFilm.remove(userId);
        likes.put(id, newLikesOfFilm);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count <= 0) {
            throw new WrongArgumentException("Недопустимое значение count");
        }
        return getFilms().stream()
                .sorted(Comparator.comparing(this::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private int getLikesCount(Film film) {
        return likes.get(film.getId()).size();
    }
}
