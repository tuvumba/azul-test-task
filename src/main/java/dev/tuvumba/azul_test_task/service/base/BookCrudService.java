package dev.tuvumba.azul_test_task.service.base;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.BookDto;
import dev.tuvumba.azul_test_task.domain.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * A base interface for Book CRUD service. Extended with book-specific queries.
 */
public interface BookCrudService extends ProductCrudService<BookDto> {
    PaginatedResponse<BookDto> findByGenre(Genre genre, Pageable pageable);
    PaginatedResponse<BookDto> findByAuthor(Author author, Pageable pageable);
    List<BookDto> findByTitle(String title);
    PaginatedResponse<BookDto> findByTitleContaining(String title, Pageable pageable);
    PaginatedResponse<BookDto> findByAuthorNameContaining(String authorName, Pageable pageable);
    BookDto updateBook(Long id, Map<String, Object> updates);
}
