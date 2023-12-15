package com.capstone.InventoryUpdate.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capstone.InventoryUpdate.model.Products;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductFeignClientFallback implements ProductFeignClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductFeignClientFallback.class);
	
	@Override
	public Products getProductDetails(int productId) {
		logger.info("Cartfallback: product service is down for this productId : {}", productId);
		return null;
	}

	@Override
	public Products updateProduct(Products product) {
		logger.info("Cartfallback: product service is down for this productId : {}", product);
		return null;
	}
}
