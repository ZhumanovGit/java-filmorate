package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int rate;
    private Set<Genre> genres;
    private Mpa mpa;

    public Film(Integer id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                int rate,
                Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.genres = new HashSet<>();
        this.mpa = mpa;
    }
}
