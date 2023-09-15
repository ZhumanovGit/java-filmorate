package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.service.ValidateService;

class InMemoryFilmStorageTest extends FilmStorageTest{
    @Override
    public FilmStorage getStorage() {
        return new InMemoryFilmStorage(new ValidateService());
    }
}
