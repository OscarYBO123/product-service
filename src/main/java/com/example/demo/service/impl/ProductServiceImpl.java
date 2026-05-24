package com.example.demo.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

/**
 * Implmenetacion de Servicio para productos
 */
@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
	@Override
	public ProductResponse create(ProductRequest request) {
		Product product = mapper.toEntity(request);

        Product saved = repository.save(product);

        return mapper.toResponse(saved);
	}

	@Override
	public List<ProductResponse> findAll() {
		return repository.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	public ProductResponse findById(Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        return mapper.toResponse(product);
	}

	@Override
	public ProductResponse update(Long id, ProductRequest request) {
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        mapper.updateEntity(product, request);

        Product updated = repository.save(product);

        return mapper.toResponse(updated);
	}

	@Override
	public void delete(Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        repository.delete(product);
	}

	@Override
	public Page<ProductResponse> findAll(Pageable pageable) {
		return repository.findAll(pageable).map(mapper::toResponse);
	}

	@Override
	public List<ProductResponse> searchByName(String name) {
		return repository.findByNameContainingIgnoreCase(name).stream().map(mapper::toResponse).toList();
	}

}
