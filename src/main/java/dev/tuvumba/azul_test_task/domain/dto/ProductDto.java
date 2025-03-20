package dev.tuvumba.azul_test_task.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ProductDto {
    @Schema(example = "2332")
    Long id;
    @Schema(example = "Very Cool Producter 9000")
    String name;
    @Schema(example = "20.99")
    BigDecimal price;
    @Schema(example = "5")
    int quantity;
}
