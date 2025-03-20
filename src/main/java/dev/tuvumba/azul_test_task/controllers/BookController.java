package dev.tuvumba.azul_test_task.controllers;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.AuthorDto;
import dev.tuvumba.azul_test_task.domain.dto.BookDto;
import dev.tuvumba.azul_test_task.domain.dto.GenreDto;
import dev.tuvumba.azul_test_task.domain.dto.PaginatedResponse;
import dev.tuvumba.azul_test_task.domain.mappers.AuthorMapper;
import dev.tuvumba.azul_test_task.domain.mappers.GenreMapper;
import dev.tuvumba.azul_test_task.service.base.BookCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@Tag(name="Book API", description = "CRUD API for managing books.")
public class BookController {

    private final BookCrudService bookCrudService;
    private final GenreMapper genreMapper;
    private final AuthorMapper authorMapper;

    public BookController(BookCrudService bookCrudService, GenreMapper genreMapper, AuthorMapper authorMapper) {
        this.bookCrudService = bookCrudService;
        this.genreMapper = genreMapper;
        this.authorMapper = authorMapper;
    }

    private Pageable preparePageable(int page, int size, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    @Operation(summary = "Create/update a book", description = "Saves a book with the provided details." +
            "Implementation notes: Authors in the bookDto will be connected to existing authors by username or created given the details." +
             "The ambiguous request error will occur when the provided author's username already exists but name differs. USERNAME is mandatory. Available only to ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookDto> save(@RequestBody BookDto book) {
        Optional<BookDto> savedBook = bookCrudService.save(book);
        return savedBook.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "Update an existing book", description = "Updates the details of an existing book. " +
            "Please note that authors not present in the database will be created. Available only to ADMIN.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"authors\": [\n" +
                                    "    { \"id\": \"1\" }\n" +
                                    "  ],\n" +
                                    "  \"price\": 2020.99,\n" +
                                    "  \"quantity\": 0\n" +
                                    "}"))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content())
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody Map<String, Object> updates) {
        /*
            Note the name conflict in between @org.springframework.web.bind.annotation.RequestBody and
            @io.swagger.v3.oas.annotations.parameters.RequestBody
         */
        BookDto updatedBook = bookCrudService.updateBook(id, updates);
        return ResponseEntity.ok(updatedBook);
    }


    @Operation(summary = "Get all books", description = "Retrieves a paginated list of all books. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<BookDto>> findAll(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name="size") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(bookCrudService.findAll(preparePageable(page, size, sortBy, ascending)));
    }

    @Operation(summary = "Get a book by ID", description = "Retrieves a book by its ID. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable Long id) {
        Optional<BookDto> book = bookCrudService.findById(id);
        return book.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get books by genre", description = "Retrieves a paginated list of books filtered by genre. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the books by genre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
    })
    @GetMapping("/genre")
    public ResponseEntity<PaginatedResponse<BookDto>> findByGenre(
            @RequestParam GenreDto genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Genre query = genreMapper.toEntity(genre);
        return ResponseEntity.ok(bookCrudService.findByGenre(query, preparePageable(page, size, sortBy, ascending)));
    }

    @Operation(summary = "Get books by author", description = "Retrieves a paginated list of books filtered by author. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the books by author",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
            @ApiResponse(responseCode = "404", description = "Author was not found.", content = @Content())
    })
    @GetMapping("/author")
    public ResponseEntity<PaginatedResponse<BookDto>> findByAuthor(
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(id);
        Author author = authorMapper.toEntity(authorDto);
        PaginatedResponse<BookDto> response = bookCrudService.findByAuthor(author, preparePageable(page, size, sortBy, ascending));
        if(response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
        {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get books by title", description = "Retrieves books by their exact title. Available to all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
    })
    @GetMapping("/title")
    public ResponseEntity<List<BookDto>> findByTitle(@RequestParam String title) {
        List<BookDto> book = bookCrudService.findByTitle(title);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @Operation(summary = "Get books by title containing a string", description = "Retrieves a paginated list of books whose titles contain the specified string. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the books by title",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
    })
    @GetMapping("/title/contains")
    public ResponseEntity<PaginatedResponse<BookDto>> findByTitleContaining(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        return ResponseEntity.ok(bookCrudService.findByTitleContaining(title, preparePageable(page, size, sortBy, ascending)));
    }


    @Operation(summary = "Get books by author name containing a string", description = "Retrieves a paginated list of books whose author's names contain the specified string. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the books by author name",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
    })
    @GetMapping("/author/contains")
    public ResponseEntity<PaginatedResponse<BookDto>> findByAuthorNameContaining(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        return ResponseEntity.ok(bookCrudService.findByAuthorNameContaining(name, preparePageable(page, size, sortBy, ascending)));
    }

    @Operation(summary = "Get books within a price range", description = "Retrieves a paginated list of books with prices between the specified range. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class)))
    })
    @GetMapping("/price")
    public ResponseEntity<PaginatedResponse<BookDto>> findPriceBetween(
            @RequestParam BigDecimal from,
            @RequestParam BigDecimal to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {


        if(from.compareTo(to) > 0) {
            //swap
            BigDecimal temp;
            temp = to;
            to = from;
            from = temp;
        }
        PaginatedResponse<BookDto> books = bookCrudService.findByPriceBetween(from, to, preparePageable(page, size, sortBy, ascending));
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    @Operation(summary = "Get books by quantity range",
            description = "Retrieves a paginated list of books with a quantity between the specified range. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid quantity range", content =  @Content())
    })
    @GetMapping("/quantity")
    public ResponseEntity<PaginatedResponse<BookDto>> findQuantityBetween(
            @RequestParam int from,
            @RequestParam int to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending){

        if(from < 0 || to < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(from > to) {
            //I've always wondered why this is not just a part of a standard library
            //I mean, there are thousands of ways of doing this and yet one would expect to write a some king of
            //utils wrapper for this
            from ^= to;
            to ^= from;
            from ^= to;
        }

        PaginatedResponse<BookDto> books = bookCrudService.findByQuantityBetween(from, to, preparePageable(page, size, sortBy, ascending));
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    @Operation(summary = "Delete a book", description = "Deletes a book by its ID. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<BookDto> book = bookCrudService.findById(id);
        if(book.isPresent()) {
            bookCrudService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

