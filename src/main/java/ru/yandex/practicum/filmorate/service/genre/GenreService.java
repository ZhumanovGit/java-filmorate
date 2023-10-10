package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
@Service
public class GenreService {
    GenreStorage storage;

    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    public List<Genre> getAll() {
        return storage.getAll();
    }

    public Genre getGenreById(int id) {
        return storage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Такого жанра не существует"));
    }

    public Genre createGenre(Genre genre) {
        if (genre.getName().isBlank()) {
            throw new ValidateException("Имя не может быть пустым");
        }
        return storage.createGenre(genre);
    }

    public void updateGenre(Genre genre) {

        storage.getGenreById(genre.getId())
                .orElseThrow(() -> new NotFoundException("Такого жанра не существует"));
        if (genre.getName().isBlank()) {
            throw new ValidateException("Имя не может быть пустым");
        }
        storage.updateGenre(genre);
    }

    public void deleteAllGenres() {
        storage.deleteAllGenres();
    }

    public void deleteGenreById(int id) {
        storage.deleteGenreById(id);
    }

}
