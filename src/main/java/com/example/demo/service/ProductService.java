package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;

/**
 * interface para productos
 */
public interface ProductService {

	ProductResponse create(ProductRequest request);

    List<ProductResponse> findAll();

    ProductResponse findById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
    
    Page<ProductResponse> findAll(Pageable pageable);

    List<ProductResponse> searchByName(String name);
    
}
