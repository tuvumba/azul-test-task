package dev.tuvumba.azul_test_task.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Genre DTO, holds the name of the genre.
 * <p>
 *  As it stands, this DTO is somewhat of limited use. <br>
 *  If in the future the Genre class is extended with metadata/additional info,
 *  then the separate DTO would be much more sensible and can be modified to suit the new needs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    @Schema(example = "Mystery")
    private String name;
}
