package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getMpa() {
        log.info("Обработка запроса с получением всех рейтингов");
        List<Mpa> mpaList = mpaService.getAll();
        log.info("Получен список рейтингов");
        return mpaList;
    }

    @GetMapping("/{mpaId}")
    public Mpa getMpaById(@PathVariable int mpaId) {
        log.info("Обработка запроса с получением рейтинга с id = {}", mpaId);
        Mpa mpa = mpaService.getMpaById(mpaId);
        log.info("Получен пользователь с id = {}", mpaId);
        return mpa;
    }

    @PostMapping()
    public Mpa createMpa(@RequestBody Mpa mpa) {
        log.info("Обработка запроса с созданием рейтинга");
        Mpa newMpa = mpaService.createMpa(mpa);
        int newMpaId = newMpa.getId();
        log.info("Создан новый рейтинг с id = {}", newMpaId);
        return newMpa;
    }

    @PutMapping()
    public Mpa updateMpa(@RequestBody Mpa mpa) {
        int mpaId = mpa.getId();
        log.info("Обработка запроса с обновлением рейтинга с id = {}", mpa.getId());
        mpaService.updateMpa(mpa);
        log.info("Обновлен рейтинг с id = {}", mpaId);
        return mpaService.getMpaById(mpaId);
    }
}
