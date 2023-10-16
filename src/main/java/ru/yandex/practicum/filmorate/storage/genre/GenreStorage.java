package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Genre createGenre(Genre genre);

    void updateGenre(Genre genre);

    Optional<Genre> getGenreById(int id);

    List<Genre> getGenresById(List<Integer> ids);

    List<Genre> getAll();

    void deleteAllGenres();

    void deleteGenreById(int id);

}
