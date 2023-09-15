package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    void deleteAllFilms();

    List<Film> getFilms();

    Film getFilmById(int id);

    public Map<Integer, Set<Integer>> getLikes();

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(Integer count);
}
