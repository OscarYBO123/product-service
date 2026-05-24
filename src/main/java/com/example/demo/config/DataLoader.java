package com.example.demo.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

@Component
public class DataLoader implements CommandLineRunner {

	private final ProductRepository repository;

    public DataLoader(ProductRepository repository) {
        this.repository = repository;
    }
    
	@Override
	public void run(String... args) throws Exception {
		
		Product product = new Product();

        product.setName("Laptop Gamer");
        product.setDescription("RTX 4060");
        product.setPrice(new BigDecimal("25000"));
        product.setStock(10);

        repository.save(product);

        System.out.println("PRODUCTO INSERTADO");
		
	}

}
