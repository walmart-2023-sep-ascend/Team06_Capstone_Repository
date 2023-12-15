package com.capstone.InventoryUpdate.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capstone.InventoryUpdate.model.Cart;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CartFeignClientFallback implements CartFeignClient {

	private static final Logger logger = LoggerFactory.getLogger(CartFeignClientFallback.class);

	@Override
	public Cart getCartDetails(int cartId) {
		logger.info("Cartfallback: cart service is down for this cartId : {}", cartId);
		return null;
	}

}
