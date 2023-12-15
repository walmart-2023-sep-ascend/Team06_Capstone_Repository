package com.capstone.CartAppService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.CartAppService.exception.CartNotFoundException;
import com.capstone.CartAppService.service.CartServiceImpl;
import com.capstone.CartAppService.model.Cart;

@RestController
public class CartController {

	@Autowired
	CartServiceImpl cserv;
	ResponseEntity<?> resentity;

	@GetMapping("/carts")
	public ResponseEntity<?> getallCarts() {
		List<Cart> clist = cserv.getAllCarts();
		resentity = new ResponseEntity<>(clist, HttpStatus.OK);
		return resentity;
	}

	@GetMapping("/cart/{cid}")
	@ExceptionHandler(CartNotFoundException.class)
	public ResponseEntity<?> getProduct(@PathVariable("cid") int cid) throws CartNotFoundException {
		try {
			Cart c = cserv.getCartbyId(cid);
			//cserv.getProductsinCart(c);
			resentity = new ResponseEntity<>(c, HttpStatus.OK);
		} catch (CartNotFoundException e) {
			throw new CartNotFoundException();
		} catch (Exception e) {
			System.out.println(e);
		}
		return resentity;
	}

}