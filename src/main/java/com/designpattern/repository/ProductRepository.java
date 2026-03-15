package com.designpattern.repository;

import com.designpattern.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class ProductRepository {

    private final Map<Long, Product> database = new HashMap<>();

    public ProductRepository() {
        database.put(1L, new Product(1L, "iPhone 15", "Electronics", 79999.00));
        database.put(2L, new Product(2L, "MacBook Pro", "Electronics", 199999.00));
        database.put(3L, new Product(3L, "Nike Air Max", "Footwear", 12999.00));
        database.put(4L, new Product(4L, "Sony Headphones", "Electronics", 29999.00));
        database.put(5L, new Product(5L, "Levi's Jeans", "Clothing", 4999.00));
        log.info("ProductRepository initialized with {} products", database.size());
    }

    public Optional<Product> findById(Long id) {
        log.info("Fetching product from DB - id: {}", id);
        return Optional.ofNullable(database.get(id));
    }

}