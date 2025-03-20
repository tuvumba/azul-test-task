package dev.tuvumba.azul_test_task.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 *  In the current implementation, Book is identical to Product, if we exclude authors and genres.
 *  In the future, we may populate it with more metadata.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto extends ProductDto {
    List<AuthorDto> authors;
    List<GenreDto> genres;
}
