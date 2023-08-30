package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    private String name;
    private LocalDate birthday;
}
