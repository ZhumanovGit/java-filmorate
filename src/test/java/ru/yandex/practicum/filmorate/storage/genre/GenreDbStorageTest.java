package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void createGenre_whenGenreIsCorrect_thanReturnGenre() {
        Genre genre = new Genre(1, "Комедия");

        Genre createdGenre = genreDbStorage.createGenre(genre);
        int genreId = createdGenre.getId();

        assertThat(createdGenre).hasFieldOrPropertyWithValue("id", genreId)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void createGenre_whenGenreWithNoId_thanReturnGenre() {
        Genre genre = Genre.builder().name("Комедия").build();

        Genre createdGenre = genreDbStorage.createGenre(genre);
        int genreId = createdGenre.getId();

        assertThat(createdGenre).hasFieldOrPropertyWithValue("id", genreId)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void updateGenre_whenCalled_thanUpdateGenre() {
        Genre genre = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        int genreId = genre.getId();
        Genre newGenre = Genre.builder()
                .id(genreId)
                .name("неКомедия")
                .build();

        genreDbStorage.updateGenre(newGenre);

        assertThat(newGenre).hasFieldOrPropertyWithValue("id", genreId)
                .hasFieldOrPropertyWithValue("name", "неКомедия");
    }

    @Test
    public void getGenreById_whenGenreIsFound_thanReturnOptionalWithGenre() {
        Genre genre = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        int genreId = genre.getId();

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(genreId);

        assertThat(optionalGenre)
                .isPresent().hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("id", genreId)
                        .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void getGenreById_whenGenreIsNotFound_thanReturnEmptyOptional() {

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(45);

        assertThat(optionalGenre).isEmpty();
    }

    @Test
    public void getAll_whenStorageHasGenres_thanReturnListOfGenres() {
        Genre firstGenre = genreDbStorage.createGenre(Genre.builder().name("Комедия").build());
        Genre secondGenre = genreDbStorage.createGenre(Genre.builder().name("Драма").build());
        Genre thirdGenre = genreDbStorage.createGenre(Genre.builder().name("Триллер").build());

        List<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isNotEmpty();
        assertThat(firstGenre).isIn(genres);
        assertThat(secondGenre).isIn(genres);
        assertThat(thirdGenre).isIn(genres);
    }

    @Test
    public void getAll_whenStorageHasNoGenres_thanReturnEmptyList() {
        genreDbStorage.deleteAllGenres();

        List<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isEmpty();
    }

    @Test
    public void deleteAllGenres_whenCalled_thanDeleteAllGenres() {
        Genre firstGenre = genreDbStorage.createGenre(Genre.builder().name("Комедия").build());
        Genre secondGenre = genreDbStorage.createGenre(Genre.builder().name("Драма").build());
        Genre thirdGenre = genreDbStorage.createGenre(Genre.builder().name("Триллер").build());

        genreDbStorage.deleteAllGenres();
        List<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isEmpty();
    }

    @Test
    public void deleteGenreById_whenCalled_thanDeleteNeedGenre() {
        Genre firstGenre = genreDbStorage.createGenre(Genre.builder().name("Комедия").build());
        int genreId = firstGenre.getId();

        genreDbStorage.deleteGenreById(genreId);
        List<Genre> genres = genreDbStorage.getAll();

        assertThat(firstGenre).isNotIn(genres);
    }


    @Test
    public void getAllGenresForFilm_whenFilmHasGenres_thanReturnListOfGenres() {
        Genre firstGenre = genreDbStorage.createGenre(new Genre(1, "Комедия"));
        Genre secondGenre = genreDbStorage.createGenre(new Genre(2, "Драма"));
        Mpa mpa = new Mpa(1, "G");
        List<Genre> genres = new ArrayList<>();
        genres.add(firstGenre);
        genres.add(secondGenre);
        Film film = Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(genres)
                .mpa(mpa)
                .build();
        Film createdFilm = filmDbStorage.createFilm(film);
        int filmId = createdFilm.getId();

        List<Genre> filmGenres = genreDbStorage.getAllGenresForFilm(filmId);

        assertThat(filmGenres).isNotEmpty();
        assertThat(firstGenre).isIn(filmGenres);
        assertThat(secondGenre).isIn(filmGenres);
    }

    @Test
    public void getAllGenresForFilm_whenFilmHasNoGenres_thanReturnEmptyList() {
        Mpa mpa = new Mpa(1, "G");
        Film film = Film.builder()
                .name("niceName")
                .description("its desc")
                .releaseDate(LocalDate.of(1967, Month.MARCH, 25))
                .duration(100)
                .genres(new ArrayList<>())
                .mpa(mpa)
                .build();
        Film createdFilm = filmDbStorage.createFilm(film);
        int filmId = createdFilm.getId();

        List<Genre> filmGenres = genreDbStorage.getAllGenresForFilm(filmId);

        assertThat(filmGenres).isEmpty();
    }
}