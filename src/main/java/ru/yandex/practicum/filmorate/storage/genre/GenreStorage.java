package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Genre createGenre(Genre genre);
    void updateGenre(Genre genre);
    Optional<Genre> getGenreById(int id);
    List<Genre> getAll();
    List<Genre> getAllGenresForFilm(int filmId);

}
