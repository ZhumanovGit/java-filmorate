package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Mpa createMpa(Mpa mpa);

    void updateMpa(Mpa mpa);

    Optional<Mpa> getMpaById(int id);

    List<Mpa> getAll();

    void deleteAllMpa();

    void deleteMpaById(int id);
}
