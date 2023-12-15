package com.capstone.CartAppService.service;

import java.util.List;
import com.capstone.CartAppService.model.Cart;

public interface CartService {
	public List<Cart> getAllCarts();
	public Cart getCartbyId(int cid);
}
