package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public List<Film> getAll() {
        return storage.getFilms();
    }

    public Film getFilmById(int id) {
        return storage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public void deleteAll() {
        storage.deleteAllFilms();
    }

    public void deleteFilmById(int id) {
        storage.deleteFilm(id);
    }

    public void likeFilm(int userId, int filmId) {
        Film film = storage.getFilmById(filmId);

        film.getLikedUsers().add((long) userId);
    }

    public void unLikeFilm(int userId, int filmId) {
        Film film = storage.getFilmById(filmId);

        film.getLikedUsers().remove((long) userId);
    }

    public List<Film> getPopularFilms() {
        return storage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
