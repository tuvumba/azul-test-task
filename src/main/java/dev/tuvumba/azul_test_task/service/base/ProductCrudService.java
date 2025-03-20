package dev.tuvumba.azul_test_task.service.base;

import dev.tuvumba.azul_test_task.domain.dto.PaginatedResponse;
import dev.tuvumba.azul_test_task.domain.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Generic product service with CRUD and paging support.
 * @param <T>
 */
public interface ProductCrudService<T extends ProductDto> extends CrudService<T, Long> {
    PaginatedResponse<T> findAll(Pageable pageable);
    PaginatedResponse<T> findByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable);
    PaginatedResponse<T> findByQuantityBetween(int from, int to, Pageable pageable);
}
