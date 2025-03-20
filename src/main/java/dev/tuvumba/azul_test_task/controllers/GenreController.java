package dev.tuvumba.azul_test_task.controllers;

import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.GenreDto;
import dev.tuvumba.azul_test_task.domain.mappers.GenreMapper;
import dev.tuvumba.azul_test_task.repository.GenreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genre")
@Tag(name = "Genre API", description = "A simple CRUD API for genres.")
public class GenreController {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreController(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Operation(summary = "Get all genres", description = "Retrieves a list of all genres. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all genres",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
    })
    @GetMapping
    public List<GenreDto> findAll() {
        return genreMapper.toDtoList(genreRepository.findAll());
    }



    @Operation(summary = "Save a new genre", description = "Creates a new genre with the provided details. Available to ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the genre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenreDto> save(@RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreMapper.toDto(genreRepository.save(genreMapper.toEntity(genreDto))));
    }

    @Operation(summary = "Get a genre by name", description = "Retrieves a genre by its name. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the genre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/find")
    public ResponseEntity<GenreDto> findById(@RequestParam String name) {
        Optional<Genre> genre = genreRepository.findById(name);
        return genre.map(value -> ResponseEntity.ok(genreMapper.toDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find genres by name", description = "Finds genres whose names contain the specified string. Available to all.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved genres",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
    })
    @GetMapping("/contains")
    public List<GenreDto> findByNameContaining(@RequestParam String name) {
        return genreMapper.toDtoList(genreRepository.findByNameIsContaining(name));
    }

    @Operation(summary = "Delete a genre", description = "Deletes a genre by its name. Available to ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the genre", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Genre not found", content = @Content())
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteById(@RequestParam String name) {
        Optional<Genre> genre = genreRepository.findById(name);
        if (genre.isPresent()) {
            genreRepository.deleteById(name);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
