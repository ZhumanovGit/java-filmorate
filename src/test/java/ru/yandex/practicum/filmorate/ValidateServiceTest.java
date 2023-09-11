package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ValidateServiceTest {
    ValidateService service = new ValidateService();

    @Test
    public void shouldCorrectlyValidateFilmForCreate() {
        Film film = Film.builder()
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        assertDoesNotThrow(() -> service.validateCreateFilm(film));
    }

    @Test
    public void shouldThrowExceptionThanFilmHasNoName() {
        Film film = Film.builder()
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Фильм не имеет названия", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionThanNameIsBlank() {
        Film film = Film.builder()
                .name("  ")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Название не может быть пустым", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfDescriptionIsLongerThan200Chars() {
        Film film = Film.builder()
                .name("testName")
                .description("very long test desc for film very long test desc for film very long test desc for " +
                        "film very long test desc for film very long test desc for film very long test desc for film " +
                        "very long test desc for film very long test desc for film very long test desc for film very " +
                        "long test desc for film very long test desc for film very long test desc for film very long " +
                        "test desc for film very long test desc for film very long test desc for film very long test " +
                        "desc for film very long test desc for film ")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Описание не может быть длиннее 200 символов", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfFilmHasBadDuration() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(0)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Продолжительность не может быть отрицательной", throwable.getMessage());

    }

    @Test
    public void shouldThrowExceptionIfFilmHasNoReleaseDate() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Фильм не может не иметь даты выхода", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfFilmReleaseDateIsBeforeThan28DecemberOf1895() {
        Film film = Film.builder()
                .id(1)
                .name("testName")
                .description("test desc for film")
                .releaseDate(LocalDate.of(1700, Month.JANUARY, 1))
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateFilm(film));

        assertEquals("Фильм не мог выйти раньше 28 декабря 1895 года", throwable.getMessage());
    }

    @Test
    public void shouldCorrectlyValidateFilmForUpdate() {
        Film film = Film.builder()
                .id(1)
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        assertDoesNotThrow(() -> service.validateUpdateFilm(film));
    }

    @Test
    public void shouldThrowExceptionIfIdOfFilmIsNotFound() {
        Film film = Film.builder()
                .name("testFilm")
                .description("test desc for film")
                .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
                .duration(120)
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateUpdateFilm(film));
        assertEquals("Не найден id для PUT запроса", throwable.getMessage());
    }

    @Test
    public void shouldCorrectlyValidateUserForCreate() {
        User user = User.builder()
                .name("testUser")
                .email("test-user@ya.ru")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        assertDoesNotThrow(() -> service.validateCreateUser(user));
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoEmail() {
        User user = User.builder()
                .name("testUser")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Пользователь не имеет почту", throwable.getMessage());

    }

    @Test
    public void shouldThrowExceptionIfUserHasBlankEmail() {
        User user = User.builder()
                .name("testUser")
                .email("   ")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Почта не может быть пустой", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoSpecCharInEmail() {
        User user = User.builder()
                .name("testUser")
                .email("asdasdasd")
                .login("testLogin")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Почта должна содержать знак @", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserHasNoLogin() {
        User user = User.builder()
                .name("testUser")
                .email("testEmail@ya.ru")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Пользователь не имеет логин", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserHasBlankLogin() {
        User user = User.builder()
                .name("testUser")
                .email("testEmail@ya.ru")
                .login("   ")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Логин не может быть пустым", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserLoginHasSpace() {
        User user = User.builder()
                .name("testUser")
                .email("testEmail@ya.ru")
                .login("bad login")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Логин не может содержать пробелы", throwable.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfBirthdayIsAfterThanNow() {
        User user = User.builder()
                .name("testUser")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2025, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateCreateUser(user));

        assertEquals("Некорректная дата рождения", throwable.getMessage());
    }

    @Test
    public void shouldCorrectlyUpdateUser() {
        User user = User.builder()
                .id(1)
                .name("test")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        assertDoesNotThrow(() -> service.validateUpdateUser(user));
    }

    @Test
    public void shouldThrowExceptionIfIdIsNullForUpdate() {
        User user = User.builder()
                .name("test")
                .email("testEmail@ya.ru")
                .login("login")
                .birthday(LocalDate.of(2021, Month.DECEMBER, 3))
                .build();

        Throwable throwable = assertThrows(ValidateException.class, () -> service.validateUpdateUser(user));

        assertEquals("Не найден id для PUT запроса", throwable.getMessage());
    }


}