package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Mpa {

    private int id;
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("rating_name", name);

        return values;
    }
}
