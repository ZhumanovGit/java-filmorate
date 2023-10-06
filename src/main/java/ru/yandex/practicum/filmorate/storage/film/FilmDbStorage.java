package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public class FilmDbStorage implements FilmStorage{
    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public void deleteFilm(int id) {

    }

    @Override
    public void deleteAllFilms() {

    }

    @Override
    public List<Film> getFilms() {
        return null;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.empty();
    }

    @Override
    public void addLike(Film film, User user) {

    }

    @Override
    public void deleteLike(Film film, User user) {

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }
}
