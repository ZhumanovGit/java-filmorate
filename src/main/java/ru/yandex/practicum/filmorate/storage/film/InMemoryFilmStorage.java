package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private int id;
    private final Map<Integer, Set<Integer>> likes;

    @Autowired
    public InMemoryFilmStorage() {
        this.films = new LinkedHashMap<>();
        this.id = 0;
        this.likes = new HashMap<>();
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
        film.setLikesCount(films.get(filmId).getLikesCount());
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
        int newLikesCount = film.getLikesCount() + 1;
        film.setLikesCount(newLikesCount);
    }

    @Override
    public void deleteLike(Film film, User user) {
        int filmId = film.getId();
        int userId = user.getId();
        Set<Integer> newLikesOfFilm = likes.get(filmId);
        newLikesOfFilm.remove(userId);
        likes.put(filmId, newLikesOfFilm);
        int newLikesCount = film.getLikesCount() - 1;
        film.setLikesCount(newLikesCount);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
