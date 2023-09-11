package ru.yandex.practicum.filmorate.exception;

public class WrongArgumentException extends RuntimeException{
    public WrongArgumentException(String s) {
        super(s);
    }
}
