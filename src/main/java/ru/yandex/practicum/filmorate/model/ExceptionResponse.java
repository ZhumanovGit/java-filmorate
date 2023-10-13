package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    final String exception;
    String stackTrace;
}
