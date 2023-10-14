package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

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

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(1000);

        assertThat(optionalGenre).isEmpty();
    }

    @Test
    public void getAll_whenStorageHasGenres_thanReturnListOfGenres() {
        genreDbStorage.deleteAllGenres();
        Genre firstGenre = genreDbStorage.createGenre(Genre.builder().name("Комедия").build());
        Genre secondGenre = genreDbStorage.createGenre(Genre.builder().name("Драма").build());
        Genre thirdGenre = genreDbStorage.createGenre(Genre.builder().name("Триллер").build());

        List<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isNotEmpty();
        assertEquals(genres.get(0).getId(), firstGenre.getId());
        assertEquals(genres.get(1).getId(), secondGenre.getId());
        assertEquals(genres.get(2).getId(), thirdGenre.getId());
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
}