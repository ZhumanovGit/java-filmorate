package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Genre {

    private int id;
    private String name;
}
