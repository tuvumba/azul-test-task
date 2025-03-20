package dev.tuvumba.azul_test_task.repository;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.Book;
import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.repository.base.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookRepository extends ProductRepository<Book> {
    List<Book> findBooksByName(String name);
    Page<Book> findBooksByAuthorsContainingIgnoreCase(Author author, Pageable pageable);
    Page<Book> findBooksByGenresContainingIgnoreCase(Genre genre,  Pageable pageable);
    Page<Book> findBooksByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     *  Query for finding books where the author(at least one of them) contains a given name.
     * @param name String to search
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE LOWER(a.displayName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Book> findBooksByAuthorNameContaining(@Param("name") String name, Pageable pageable);
}
