package com.designpattern.service;

import com.designpattern.model.Product;
import com.designpattern.repository.ProductRepository;
import com.designpattern.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public Product getProductById(Long id) {
        log.info("Real service - fetching product id: {}", id);

        return productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product not found - id: {}", id);
            return new RuntimeException(Constants.PRODUCT_NOT_FOUND + id);
        });
    }

}