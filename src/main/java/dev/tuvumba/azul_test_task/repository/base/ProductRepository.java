package dev.tuvumba.azul_test_task.repository.base;

import dev.tuvumba.azul_test_task.domain.base.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigDecimal;


/**
 * This is a base template for a generic repository containing Products
 * @param <T> type of product to store
 */
@NoRepositoryBean
public interface ProductRepository<T extends Product> extends JpaRepository<T, Long> {
    Page<T> findByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable);
    Page<T> findByQuantityBetween(int from, int to, Pageable pageable);
}

