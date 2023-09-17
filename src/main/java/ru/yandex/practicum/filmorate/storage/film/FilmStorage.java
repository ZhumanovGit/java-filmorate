package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Film createFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(int id);

    void deleteAllFilms();

    List<Film> getFilms();

    Optional<Film> getFilmById(int id);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getPopularFilms(Integer count);
}
