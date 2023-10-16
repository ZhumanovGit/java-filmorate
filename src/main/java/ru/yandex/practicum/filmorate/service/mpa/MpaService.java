package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {

    MpaStorage storage;

    @Autowired
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    public List<Mpa> getAll() {
        return storage.getAll();
    }

    public Mpa getMpaById(int id) {
        return storage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));
    }

    public Mpa createMpa(Mpa mpa) {
        if (mpa.getName().isBlank()) {
            throw new ValidateException("Имя не может быть пустым");
        }
        return storage.createMpa(mpa);
    }

    public void updateMpa(Mpa mpa) {
        if (mpa.getName().isBlank()) {
            throw new ValidateException("Имя не может быть пустым");
        }
        storage.getMpaById(mpa.getId())
                .orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));

        storage.updateMpa(mpa);
    }

    public void deleteAllMpa() {
        storage.deleteAllMpa();
    }

    public void deleteMpaById(int id) {
        storage.deleteMpaById(id);
    }


}
