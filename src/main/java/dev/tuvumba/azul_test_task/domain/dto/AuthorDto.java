package dev.tuvumba.azul_test_task.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  I've decided to include IDs in DTOs because we need them for inventory keeping reasons (authors with the same name).
 *  If this was exposed to the public, I would avoid giving out the direct database ID's.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(example = "1")
    public Long id;
    @Schema(example = "Robert C. Martin")
    public String displayName;
    @Schema(example = "Software engineer and author of Clean Code.")
    public String description;
}
