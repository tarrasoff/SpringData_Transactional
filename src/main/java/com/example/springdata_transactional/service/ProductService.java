package com.example.springdata_transactional.service;

import com.example.springdata_transactional.entity.Product;
import com.example.springdata_transactional.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }
    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    @Transactional(propagation = Propagation.NESTED)
    public void decreaseProductQuantities(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        for (Product product : products) {
            product.setQuantity(product.getQuantity() - 1);
        }
        productRepository.saveAll(products);
    }
}