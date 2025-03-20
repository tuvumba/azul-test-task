package dev.tuvumba.azul_test_task.service;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.Book;
import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.BookDto;
import dev.tuvumba.azul_test_task.domain.dto.GenreDto;
import dev.tuvumba.azul_test_task.domain.dto.PaginatedResponse;
import dev.tuvumba.azul_test_task.domain.mappers.AuthorMapper;
import dev.tuvumba.azul_test_task.domain.mappers.BookMapper;
import dev.tuvumba.azul_test_task.domain.mappers.GenreMapper;
import dev.tuvumba.azul_test_task.repository.AuthorRepository;
import dev.tuvumba.azul_test_task.repository.BookRepository;
import dev.tuvumba.azul_test_task.repository.GenreRepository;
import dev.tuvumba.azul_test_task.service.base.BookCrudService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookCrudServiceImpl implements BookCrudService {

    // logging
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BookCrudServiceImpl.class);

    // repositories
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    // mappers
    private final BookMapper bookMapper;
    private final GenreMapper genreMapper;
    private final AuthorMapper authorMapper;

    @Autowired
    public BookCrudServiceImpl(BookRepository bookRepository, BookMapper bookMapper, AuthorRepository authorRepository, GenreRepository genreRepository, EntityManager entityManager, BookMapper bookMapper1, GenreMapper genreMapper, AuthorMapper authorMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookMapper = bookMapper1;
        this.genreMapper = genreMapper;
        this.authorMapper = authorMapper;
    }

    private PaginatedResponse<BookDto> bookPageToBookDtoPaginatedResponse(Page<Book> books) {
        return new PaginatedResponse<>(bookMapper.toDtoList(books.getContent()),
                books.getTotalPages(), books.getTotalElements(), books.getNumber(), books.getSize());
    }

    /*
     Implementation note:
     My first idea was to create authors 'on the go', when the user tries to save the book.
     I even got it working, with correct assigning to the existing authors (by username and display name).
     However, it appeared to me that this way is unreliable and brings a lot of confusion when dealing with duplicates.

     Especially this case:
     Imagine we have Stephen King (stephenking) and the API gets the request for Stephen King.
     Do we assume that it directly refers to the one we have or is there a possibility of a duplicate name?
     This reminded me of how on Spotify random songs end up under big artists just because of the name clash.

     Plus, making the client responsible for making sure the username if available and without collisions would
     be quite an anti pattern, especially considering SoC.

     So, I've decided to ditch this idea and go back to ID's, forcing the client to add authors first.


    private Author findOrCreateAuthor(String username, String displayName) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Author's username cannot be empty.");
        }

        Optional<Author> existingAuthor = authorRepository.findById(username);

        if (existingAuthor.isPresent()) {
            Author author = existingAuthor.get();

            if (displayName != null && !author.getDisplayName().equals(displayName)) {
                logger.error("Author with username \"{}\" already exists but has a different name: \"{}\" (existing), \"{}\" (sent)",
                        username, author.getDisplayName(), displayName);
                throw new IllegalArgumentException("Ambiguous request: Author with username " + username +
                        " already exists but has a different name.");
            }

            logger.debug("Found existing author: \"{}\" named \"{}\"", username, author.getDisplayName());
            return author;
        } else {
            logger.debug("Creating new author with username \"{}\" and name \"{}\"", username, displayName);
            Author newAuthor = new Author();
            newAuthor.setUsername(username);
            newAuthor.setDisplayName(displayName);
            authorRepository.save(newAuthor);
            logger.debug("Created new author: \"{}\" named \"{}\"", username, displayName);
            return newAuthor;
        }
    }
    */

    private Genre findOrCreateGenre(String genreName) {
        Optional<Genre> existingGenre = genreRepository.findById(genreName);
        if (existingGenre.isEmpty()) {
            logger.debug("Creating genre with name {}", genreName);
            Genre newGenre = new Genre();
            newGenre.setName(genreName);
            genreRepository.save(newGenre);
            logger.debug("Created new genre: \"{}\"", newGenre.getName());
            return newGenre;
        } else {
            logger.debug("Found existing genre: \"{}\"", existingGenre.get().getName());
            return existingGenre.get();
        }
    }

    @Override
    @Transactional
    public Optional<BookDto> save(BookDto book) {
        try {
            // handle authors
            List<Author> updatedAuthors = book.getAuthors().stream()
                    .map(author -> authorRepository.findById(author.getId()).orElseThrow(() -> new EntityNotFoundException("Author(s) of the book has not been found. Add them before adding a book.")))
                    .distinct()
                    .collect(Collectors.toList());

            // handle genres
            List<Genre> updatedGenres = book.getGenres().stream().map(genre -> findOrCreateGenre(genre.getName())).collect(Collectors.toList());

            book.setAuthors(authorMapper.toDtoList(updatedAuthors));
            book.setGenres(genreMapper.toDtoList(updatedGenres));

            Optional<Book> repositoryBook = bookRepository.findById(book.getId());
            if (repositoryBook.isPresent()) {
                logger.debug("Book \"{}\" with ISBN \"{}\" exists", book.getName(), book.getId());
            }

            Book savedBook = bookRepository.save(bookMapper.toEntity(book));
            return Optional.of(bookMapper.toDto(savedBook));
        } catch (OptimisticLockException | IllegalArgumentException e) {
            logger.error("Saving a book failed: \"{}\"", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Handles the book update via a map of "property" -> 'new value' <br>
     * Note: If an author is not found, it WILL NOT BE CREATED. If a genre was not found, it will be created.
     *
     * @param id A book with this Id will be updated.
     * @param updates Map 'field' -> 'new value' of changes that will be applied
     * @return An updated BookDto instance.
     *
     * @throws  RuntimeException if the update failed.
     */
    public BookDto updateBook(Long id, Map<String, Object> updates) throws EntityNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        updates.forEach((key, value) -> {
            try {
                if (value == null) {
                    throw new IllegalArgumentException("Field '" + key + "' cannot be null.");
                }

                switch (key) {
                    case "price" -> {
                        if (value instanceof Number || value instanceof String) {
                            book.setPrice(new BigDecimal(value.toString()));
                            logger.debug("Updated price with value \"{}\"", value);
                        } else {
                            throw new IllegalArgumentException("Invalid type for 'price'. Expected a number.");
                        }
                    }
                    case "quantity" -> {
                        if (value instanceof Number) {
                            book.setQuantity(((Number) value).intValue());
                            logger.debug("Updated quantity with value \"{}\"", value);
                        } else {
                            throw new IllegalArgumentException("Invalid type for 'quantity'. Expected an integer.");
                        }
                    }
                    case "name" -> {
                        if (value instanceof String name && !name.isBlank()) {
                            book.setName(name);
                            logger.debug("Updated name with value \"{}\"", name);
                        } else {
                            throw new IllegalArgumentException("Invalid type for 'name'. Expected a non-empty string.");
                        }
                    }
                    case "authors" -> {
                        if (value instanceof List<?> authorDtos) {
                            List<Author> updatedAuthors = new ArrayList<>();

                            for (Object obj : authorDtos) {
                                if (!(obj instanceof Map<?, ?> authorData)) {
                                    throw new IllegalArgumentException("Invalid author format. Expected an object with 'username' and 'displayName'.");
                                }

                                Long authorId = ((Number) authorData.get("id")).longValue();
                                updatedAuthors.add(authorRepository.findById(authorId).orElseThrow(() -> new EntityNotFoundException("Author(s) of the book has not been found. Add them before adding a book.")));
                            }

                            // remove duplicates and set updated authors
                            book.setAuthors(updatedAuthors.stream().distinct().collect(Collectors.toList()));
                            logger.debug("Updated authors for book: \"{}\", new authors are \"{}\"", book.getName(), book.getAuthors());
                        } else {
                            throw new IllegalArgumentException("Invalid type for 'authors'. Expected a list of author objects.");
                        }
                    }
                    case "genres" -> {
                        if (value instanceof List<?> genreNames) {
                            List<String> names = genreNames.stream()
                                    .filter(v -> v instanceof String)
                                    .distinct()
                                    .map(Object::toString)
                                    .toList();

                            List<Genre> updatedGenres = names.stream().map(this::findOrCreateGenre).toList();
                            book.setGenres(updatedGenres);
                        } else {
                            throw new IllegalArgumentException("Invalid type for 'genres'. Expected a list of genre names.");
                        }
                    }
                    default -> throw new IllegalArgumentException("Unknown field: " + key);
                }
            } catch (Exception e) {
                logger.error("Error updating '{}': {}", key, e.getMessage());
                throw new RuntimeException("Error updating '" + key + "': " + e.getMessage());
            }
        });

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public PaginatedResponse<BookDto> findByGenre(Genre genre, Pageable pageable) {
        Page<Book> books = bookRepository.findBooksByGenresContainingIgnoreCase(genre, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }

    @Override
    public PaginatedResponse<BookDto> findByAuthor(Author author, Pageable pageable) {
        Page<Book> books = bookRepository.findBooksByAuthorsContainingIgnoreCase(author, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }

    @Override
    public List<BookDto> findByTitle(String title) {
        return bookMapper.toDtoList(bookRepository.findBooksByName(title));
    }

    @Override
    public PaginatedResponse<BookDto> findByTitleContaining(String title, Pageable pageable) {
        Page<Book> books = bookRepository.findBooksByNameContainingIgnoreCase(title, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }

    @Override
    public PaginatedResponse<BookDto> findByAuthorNameContaining(String authorName, Pageable pageable) {
        Page<Book> books = bookRepository.findBooksByAuthorNameContaining(authorName, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }


    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse<BookDto> findAll(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }


    @Override
    public PaginatedResponse<BookDto> findByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable) {
        Page<Book> books = bookRepository.findByPriceBetween(from, to, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }

    @Override
    public PaginatedResponse<BookDto> findByQuantityBetween(int from, int to, Pageable pageable) {
        Page<Book> books = bookRepository.findByQuantityBetween(from, to, pageable);
        return bookPageToBookDtoPaginatedResponse(books);
    }

    @Override
    public Optional<BookDto> findById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toDto);
    }


    @Override
    @Transactional
    public void delete(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            Book managedBook = book.get();
            List<Author> authorsToCheck = new ArrayList<>(managedBook.getAuthors());
            for (Author author : authorsToCheck) {
                logger.debug("Processing author {} called {}", author.getId(), author.getDisplayName());

                Author managedAuthor = authorRepository.findById(author.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Author not found"));

                managedAuthor.getBooks().remove(managedBook);

                // delete an author if he doesn't have any books. (not by quantity, but by book_author table.)
                if (managedAuthor.getBooks().isEmpty()) {
                    authorRepository.deleteById(managedAuthor.getId());
                    logger.debug("Removed author {}", managedAuthor.getDisplayName());
                } else {
                    logger.debug("Did not remove author {}(id{}), their books are {}", managedAuthor.getDisplayName(), managedAuthor.getId(), managedAuthor.getBooks());
                }
            }
            logger.debug("Deleted book '{}'", bookId);
            bookRepository.deleteById(bookId);
        }
    }


}
