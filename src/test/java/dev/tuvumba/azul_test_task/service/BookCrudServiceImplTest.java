package dev.tuvumba.azul_test_task.service;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.Book;
import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.AuthorDto;
import dev.tuvumba.azul_test_task.domain.dto.BookDto;
import dev.tuvumba.azul_test_task.domain.dto.GenreDto;
import dev.tuvumba.azul_test_task.domain.mappers.AuthorMapper;
import dev.tuvumba.azul_test_task.domain.mappers.BookMapper;
import dev.tuvumba.azul_test_task.domain.mappers.GenreMapper;
import dev.tuvumba.azul_test_task.repository.AuthorRepository;
import dev.tuvumba.azul_test_task.repository.BookRepository;
import dev.tuvumba.azul_test_task.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCrudServiceImplTest {

    @InjectMocks
    private BookCrudServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private GenreMapper genreMapper;

    private Author existingAuthor;
    private Genre existingGenre;
    private BookDto bookDto;
    private Book bookEntity;

    @BeforeEach
    void setUp() {
        existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setDisplayName("Robert C. Martin");

        existingGenre = new Genre();
        existingGenre.setName("Programming");

        bookDto = new BookDto();
        bookDto.setId(9780136083238L);
        bookDto.setName("Clean Code");
        bookDto.setAuthors(List.of(new AuthorDto(1L, "Robert C. Martin", "A very cool guy.")));
        bookDto.setGenres(List.of(new GenreDto("Programming")));
        bookDto.setQuantity(5);
        bookDto.setPrice(BigDecimal.valueOf(20.99));

        bookEntity = new Book();
        bookEntity.setId(9780136083238L);
        bookEntity.setName("Clean Code");
        bookEntity.setQuantity(5);
        bookEntity.setAuthors(List.of(existingAuthor));
        bookEntity.setGenres(List.of(existingGenre));
        bookEntity.setPrice(BigDecimal.valueOf(20.99));
    }

     /*
       Author's note: There is additional testing needed, especially with deleting and data consistency. It was just a matter of time.
     */

    // testing save()

    @Test
    void shouldSaveBookSuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(genreRepository.findById("Programming")).thenReturn(Optional.of(existingGenre));

        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);
        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(bookEntity);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        Optional<BookDto> savedBook = bookService.save(bookDto);

        assertThat (savedBook).isPresent();
        assertThat(savedBook.get().getId()).isEqualTo(9780136083238L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldFailToSaveWhenAuthorDoesNotExist() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.save(bookDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Author(s) of the book has not been found. Add them before adding a book.");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldSaveBookWithNewlyCreatedGenre() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(genreRepository.findById("Programming")).thenReturn(Optional.empty());

        Genre newGenre = new Genre();
        newGenre.setName("Programming");

        when(genreRepository.save(any(Genre.class))).thenReturn(newGenre);
        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);
        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(bookEntity);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        Optional<BookDto> savedBook = bookService.save(bookDto);

        assertThat(savedBook).isPresent();
        verify(genreRepository).save(any(Genre.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(genreRepository.findById("Programming")).thenReturn(Optional.of(existingGenre));
        when(bookMapper.toEntity(bookDto)).thenReturn(bookEntity);

        doThrow(new IllegalArgumentException("Invalid book data"))
                .when(bookRepository).save(any(Book.class));

        Optional<BookDto> savedBook = bookService.save(bookDto);

        assertThat(savedBook).isEmpty();
        verify(bookRepository).save(any(Book.class));
    }

    // testing update()

    @Test
    void shouldUpdateBookPriceSuccessfully() {
        Map<String, Object> updates = Map.of("price", new BigDecimal("50.00"));
        bookEntity.setPrice(new BigDecimal("45.00"));
        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));


        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book bookToSave = invocation.getArgument(0);
            bookToSave.setPrice(new BigDecimal("50.00"));
            return bookToSave;
        });

        when(bookMapper.toDto(any(Book.class))).thenAnswer(invocation -> {
            Book updatedBook = invocation.getArgument(0);
            BookDto updatedDto = new BookDto();
            updatedDto.setPrice(updatedBook.getPrice());
            updatedDto.setId(updatedBook.getId());
            return updatedDto;
        });

        BookDto updatedBook = bookService.updateBook(bookDto.getId(), updates);

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getPrice()).isEqualTo(new BigDecimal("50.00"));
        verify(bookRepository).save(bookEntity);
    }

    @Test
    void shouldThrowExceptionForInvalidPriceType() {
        Map<String, Object> updates = Map.of("price", "invalidPrice");

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));

        assertThatThrownBy(() -> bookService.updateBook(bookDto.getId(), updates))
                .hasMessageContaining("Character i is neither a decimal digit number, decimal point, nor \"e\" notation exponential mark.");
    }

    @Test
    void shouldUpdateBookQuantitySuccessfully() {
        Map<String, Object> updates = Map.of("quantity", 10);

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);

        when(bookMapper.toDto(any(Book.class))).thenAnswer(invocation -> {
            Book updatedBook = invocation.getArgument(0);
            BookDto updatedDto = new BookDto();
            updatedDto.setQuantity(updatedBook.getQuantity());
            return updatedDto;
        });

        BookDto updatedBook = bookService.updateBook(bookDto.getId(), updates);

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getQuantity()).isEqualTo(10);
        verify(bookRepository).save(bookEntity);
    }

    @Test
    void shouldThrowExceptionForUnknownField() {
        Map<String, Object> updates = Map.of("nonExistingField", "value");

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));

        assertThatThrownBy(() -> bookService.updateBook(bookDto.getId(), updates))
                .hasMessageContaining("Error updating 'nonExistingField': Unknown field: nonExistingField");
    }

    @Test
    void shouldThrowExceptionForInvalidAuthorFormat() {
        Map<String, Object> updates = Map.of(
                "authors", List.of("invalidAuthorData")
        );

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));

        assertThatThrownBy(() -> bookService.updateBook(bookDto.getId(), updates))
                .hasMessageContaining("Invalid author format. Expected an object with 'username' and 'displayName'.");
    }

    @Test
    void shouldThrowExceptionForAuthorNotFound() {
        Map<String, Object> updates = Map.of(
                "authors", List.of(Map.of("id", 999L))
        );

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(bookDto.getId(), updates))
                .hasMessageContaining("Author(s) of the book has not been found.");
    }

    @Test
    void shouldRemoveDuplicateAuthors() {
        Map<String, Object> updates = Map.of(
                "authors", List.of(Map.of("id", 1L), Map.of("id", 1L))
        );

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        BookDto updatedBook = bookService.updateBook(bookDto.getId(), updates);

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getAuthors().size()).isEqualTo(1);
        verify(bookRepository).save(bookEntity);
    }

    @Test
    void shouldUpdateGenresSuccessfully() {
        Map<String, Object> updates = Map.of(
                "genres", List.of("Programming", "Java")
        );

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookEntity));
        when(genreRepository.findById("Programming")).thenReturn(Optional.of(existingGenre));
        when(genreRepository.findById("Java")).thenReturn(Optional.empty());

        bookService.updateBook(bookDto.getId(), updates);

        Genre updatedGenre = new Genre();
        updatedGenre.setName("Java");
        verify(genreRepository).save(updatedGenre);
    }


}