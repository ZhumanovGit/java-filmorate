package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    final Map<Integer, Film> films;
    int id;
    Map<Integer, Set<Integer>> likes;

    @Autowired
    public InMemoryFilmStorage() {
        this.films = new LinkedHashMap<>();
        this.id = 0;
        this.likes = new HashMap<>();
    }
    Map<Integer, Set<Integer>> getLikes() {
        return likes;
    }

    int createId() {
        return ++id;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(createId());

        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        int filmId = film.getId();
        films.put(filmId, film);
        if (likes.get(filmId) == null) {
            likes.put(filmId, new HashSet<>());
        }
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
        likes.remove(id);
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
        likes.clear();
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Film film, User user) {
        int filmId = film.getId();
        int userId = user.getId();
        Set<Integer> newLikesOfFilm = likes.get(filmId);
        newLikesOfFilm.add(userId);
        likes.put(filmId, newLikesOfFilm);
    }

    @Override
    public void deleteLike(Film film, User user) {
        int filmId = film.getId();
        int userId = user.getId();
        Set<Integer> newLikesOfFilm = likes.get(filmId);
        newLikesOfFilm.remove(userId);
        likes.put(filmId, newLikesOfFilm);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
