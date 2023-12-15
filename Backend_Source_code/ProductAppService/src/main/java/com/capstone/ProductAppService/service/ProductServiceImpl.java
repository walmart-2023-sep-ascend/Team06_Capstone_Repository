package com.capstone.ProductAppService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.ProductAppService.exception.ProductNotFoundException;
import com.capstone.ProductAppService.model.Products;
import com.capstone.ProductAppService.respository.ProductRepository;

@Service
public class ProductServiceImpl {
	@Autowired
	ProductRepository prepo;

	int pid,quantity;

	public List<Products> getAllProducts() {
		return prepo.findAll();
	}

	public Products getProductbyId(int pid) throws ProductNotFoundException {
		Products p = null;
		Optional<Products> opt = this.prepo.findByproductId(pid);
		if (opt.isPresent()) {
			p = opt.get();

		} else

			throw new ProductNotFoundException();
		return p;
	}
	public Products updateProduct(Products product) throws ProductNotFoundException {
		pid=product.getProductId();
		quantity=product.getAvailableQty();
		int result=prepo.updateQuantity(pid, quantity);
		Products p=getProductbyId(pid);
		return p;
	}
}
