package dev.tuvumba.azul_test_task.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *
 * A generic paginated response wrapper. <br>
 * Includes: totalPages, totalElements, page and size.
 * @param <T> Response will hold a list of elements of this class.
 */


@Data
@AllArgsConstructor
@Setter
public class PaginatedResponse<T> {
    private List<T> list;
    private long totalPages;
    private long totalElements;
    private int page;
    private int size;

    public PaginatedResponse(Page<T> page) {
        this.list = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber();
        this.size = page.getSize();
    }
}
