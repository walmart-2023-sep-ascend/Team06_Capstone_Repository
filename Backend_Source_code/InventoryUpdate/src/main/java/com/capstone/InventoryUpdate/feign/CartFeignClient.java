package com.capstone.InventoryUpdate.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.capstone.InventoryUpdate.model.Cart;

@FeignClient(name = "cart-service", url = "${capstone.cartservice.url}", fallback = CartFeignClientFallback.class)
public interface CartFeignClient {
	
	@GetMapping("/cart/{cartId}")
	Cart getCartDetails(@PathVariable("cartId") int cartId);
}
