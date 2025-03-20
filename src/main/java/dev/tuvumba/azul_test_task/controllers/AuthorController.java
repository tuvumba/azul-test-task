package dev.tuvumba.azul_test_task.controllers;


import dev.tuvumba.azul_test_task.config.exceptions.ApiErrorResponse;
import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.dto.AuthorDto;
import dev.tuvumba.azul_test_task.domain.mappers.AuthorMapper;
import dev.tuvumba.azul_test_task.repository.AuthorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/author")
@Tag(name = "Author API", description = "A simplistic API for basic operations regarding Authors.")
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorController(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Operation(summary = "Get all authors", description = "Fetches a list of all authors. Accessible by everyone.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of authors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDto.class))),
    })
    @GetMapping
    public List<AuthorDto> findAll() {
        return authorMapper.toDtoList(authorRepository.findAll());
    }


    @Operation(summary = "Get authors by name",
               description = "Fetches authors whose name contains the provided string (case-insensitive) Accessible by everyone.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the authors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/find")
    public List<AuthorDto> findAllByNameContaining(@RequestParam String name) {
        return authorMapper.toDtoList(authorRepository.findByDisplayNameContainingIgnoreCase(name));
    }

    @Operation(summary = "Get an author by ID",
               description = "Fetches an author by their unique username. Accessible by everyone.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDto.class))),
            @ApiResponse(responseCode = "404", description = "Author not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> findById(@PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.map(value -> ResponseEntity.status(200).body(authorMapper.toDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Save a new author",
               description = "Creates a new author with the provided details. Accessible by ADMIN.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorDto> save(@RequestBody AuthorDto authorDto) {
        if(authorDto.getId() == null)
            throw new RuntimeException("Author's ID is empty");
        return ResponseEntity.status(201).body(authorMapper.toDto(authorRepository.save(authorMapper.toEntity(authorDto))));
    }

    @Operation(summary = "Delete an author by ID",
               description = "Deletes an author by their unique ID. Accessible by ADMIN.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the author", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Author was not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteById(@RequestParam Long id) {
        if(authorRepository.findById(id).isPresent()) {
            authorRepository.deleteById(id);
            return ResponseEntity.ok("Successfully deleted the author");
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
