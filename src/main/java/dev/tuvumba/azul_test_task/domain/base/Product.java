package dev.tuvumba.azul_test_task.domain.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The base class for all products.
 */
@MappedSuperclass
@Data
public abstract class Product {

    protected String name;
    protected BigDecimal price;
    protected int quantity;

    public void setPrice(BigDecimal price) {
        // In current implementation, we allow the price of ZERO. (Gift, Promotional stunt, etc.)
        // price is >= 0
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        } else {
            this.price = price;
        }
    }
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
}
