package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.WrongArgumentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;
    
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film);
        log.info("Фильм с id = {} создан и добавлен в библиотеку", newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film newFilm = filmStorage.updateFilm(film);
        log.info("Фильм с id = {} обновлен", newFilm.getId());
        return newFilm;
    }

    public void deleteAll() {
        filmStorage.deleteAllFilms();
    }

    public void deleteFilmById(int id) {
        filmStorage.deleteFilm(id);
    }

    public void likeFilm(int userId, int filmId) {
        Film film = getFilmById(filmId);

        userStorage.getUserById(userId);

        film.getLikedUsers().add((long) userId);
    }

    public void unLikeFilm(int userId, int filmId) {
        Film film = getFilmById(filmId);

        userStorage.getUserById(userId);

        film.getLikedUsers().remove((long) userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count <= 0) {
            throw new WrongArgumentException("Недопустимое значение count");
        }
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}