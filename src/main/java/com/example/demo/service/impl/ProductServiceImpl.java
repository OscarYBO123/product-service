package com.example.demo.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.client.InventoryClient;
import com.example.demo.client.dto.InventoryResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Implmenetacion de Servicio para productos
 */
@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository repository;
    private final ProductMapper mapper;
    //Se inyecta Cliente 
    private final InventoryClient inventoryClient;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, InventoryClient inventoryClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.inventoryClient = inventoryClient;
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
	@CircuitBreaker(
			name = "inventoryService",
			fallbackMethod = "inventoryFallback")
	public ProductResponse findById(Long id) {
		InventoryResponse inventory = inventoryClient.getInventory(id);
		
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        ProductResponse response = mapper.toResponse(product);
        response.setInStock(inventory.getInStock());
        response.setQuantity(inventory.getQuantity());
        
        return response;
	}
	
	public ProductResponse inventoryFallback(Long id, Exception ex) {

	    Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

	    ProductResponse response = mapper.toResponse(product);

	    response.setQuantity(0);
	    response.setInStock(false);

	    return response;
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
