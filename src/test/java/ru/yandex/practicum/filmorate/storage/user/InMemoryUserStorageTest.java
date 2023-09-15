package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.service.ValidateService;

public class InMemoryUserStorageTest extends UserStorageTest{

    @Override
    public UserStorage getStorage() {
        return new InMemoryUserStorage(new ValidateService());
    }
}
