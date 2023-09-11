package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    void deleteAllFilms();

    List<Film> getFilms();

    Film getFilmById(int id);
}
