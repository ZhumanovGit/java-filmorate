package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Integer id;
    @NonNull
    private final String name;
    private String description;
    @NonNull
    private final LocalDate releaseDate;
    @NonNull
    private final Duration duration;
}
