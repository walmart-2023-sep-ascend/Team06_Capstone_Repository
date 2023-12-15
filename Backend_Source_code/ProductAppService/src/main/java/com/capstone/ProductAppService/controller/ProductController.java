package com.capstone.ProductAppService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.ProductAppService.exception.ProductNotFoundException;
import com.capstone.ProductAppService.model.Products;
import com.capstone.ProductAppService.service.ProductServiceImpl;

@RestController
public class ProductController {

	@Autowired
	ProductServiceImpl piserv;
	ResponseEntity<?> resentity;

	@GetMapping("/products")
	public ResponseEntity<?> getProducts() {
		List<Products> plist = piserv.getAllProducts();
		resentity = new ResponseEntity<>(plist, HttpStatus.OK);
		return resentity;
	}

	@GetMapping("/product/{pid}")
	@ExceptionHandler(ProductNotFoundException.class)
	public Products getProduct(@PathVariable("pid") int pid) throws ProductNotFoundException {
		Products product = null;
		try {
			product = piserv.getProductbyId(pid);
		} catch (ProductNotFoundException e) {
			throw new ProductNotFoundException();
		} catch (Exception e) {
			System.out.println(e);
		}
		return product;
	}

	@PutMapping("/updateProduct")
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<?> updateProduct(@RequestBody Products product) throws ProductNotFoundException {
		try {
			System.out.println("inside update product" + product);
			Products p = piserv.updateProduct(product);
			resentity = new ResponseEntity<>(p, HttpStatus.OK);
		} catch (ProductNotFoundException e) {
			throw new ProductNotFoundException();

		} catch (Exception e) {
			System.out.println(e);
		}
		return resentity;
	}
}
